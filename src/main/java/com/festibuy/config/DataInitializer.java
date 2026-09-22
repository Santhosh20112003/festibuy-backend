package com.festibuy.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.festibuy.entity.Shop;
import com.festibuy.repository.ShopRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ShopRepository shopRepository;

    @Override
    public void run(String... args) {
        if (shopRepository.count() == 0) {
            log.info("Seeding 5 sample shops for PostGIS POC...");

            List<Shop> sampleShops = List.of(
                Shop.builder()
                    .name("Fresh Mart")
                    .address("12 Mount Road, Teynampet, Chennai")
                    .latitude(13.0580)
                    .longitude(80.2430)
                    .build(),
                Shop.builder()
                    .name("Daily Needs")
                    .address("45 Sterling Road, Nungambakkam, Chennai")
                    .latitude(13.0560)
                    .longitude(80.2200)
                    .build(),
                Shop.builder()
                    .name("Organic Greens")
                    .address("88 TTK Road, Alwarpet, Chennai")
                    .latitude(13.0450)
                    .longitude(80.2500)
                    .build(),
                Shop.builder()
                    .name("City Supermarket")
                    .address("102 Jawaharlal Nehru Salai, Vadapalani, Chennai")
                    .latitude(13.0380)
                    .longitude(80.2150)
                    .build(),
                Shop.builder()
                    .name("Outstation Superstore")
                    .address("500 GST Road, Tambaram, Chennai")
                    .latitude(12.9249)
                    .longitude(80.1000)
                    .build()
            );

            shopRepository.saveAll(sampleShops);
            log.info("Successfully seeded {} sample shops.", sampleShops.size());
        } else {
            log.info("Shops table already contains data, skipping seeding.");
        }
    }
}
