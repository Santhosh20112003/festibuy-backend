package com.festibuy.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.festibuy.dto.NearbyShopResponse;
import com.festibuy.dto.ShopRequestDto;
import com.festibuy.entity.Shop;
import com.festibuy.exception.ResourceNotFoundException;
import com.festibuy.repository.ShopRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;

    @Transactional(readOnly = true)
    public List<NearbyShopResponse> findNearbyShops(double lat, double lng, double radiusKm) {
        log.info("Searching nearby shops for lat: {}, lng: {}, radiusKm: {}", lat, lng, radiusKm);
        double radiusMeters = radiusKm * 1000.0;
        return shopRepository.findNearbyShops(lat, lng, radiusMeters)
                .stream()
                .map(NearbyShopResponse::fromProjection)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Shop> getAllShops() {
        return shopRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Shop getShopById(Long id) {
        return shopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found with id: " + id));
    }

    @Transactional
    public Shop createShop(ShopRequestDto request) {
        Shop shop = Shop.builder()
                .name(request.getName())
                .address(request.getAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build();
        return shopRepository.save(shop);
    }

    @Transactional
    public List<Shop> createShopsBulk(List<ShopRequestDto> requests) {
        log.info("Bulk importing {} shops", requests.size());
        List<Shop> shops = requests.stream()
                .map(request -> Shop.builder()
                        .name(request.getName())
                        .address(request.getAddress())
                        .latitude(request.getLatitude())
                        .longitude(request.getLongitude())
                        .build())
                .toList();
        return shopRepository.saveAll(shops);
    }
}
