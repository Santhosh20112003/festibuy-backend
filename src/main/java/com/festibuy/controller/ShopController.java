package com.festibuy.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.festibuy.dto.NearbyShopResponse;
import com.festibuy.dto.ShopRequestDto;
import com.festibuy.entity.Shop;
import com.festibuy.service.ShopService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/shops")
@RequiredArgsConstructor
@Validated
@Tag(name = "Shops", description = "Shop and Geospatial Location APIs")
public class ShopController {

    private final ShopService shopService;

    @GetMapping("/nearby")
    @Operation(summary = "Find nearby shops within given radius using PostGIS")
    public ResponseEntity<List<NearbyShopResponse>> getNearbyShops(
            @RequestParam @NotNull(message = "Latitude is required")
            @DecimalMin("-90.0") @DecimalMax("90.0") Double lat,
            
            @RequestParam @NotNull(message = "Longitude is required")
            @DecimalMin("-180.0") @DecimalMax("180.0") Double lng,
            
            @RequestParam(defaultValue = "5.0")
            @DecimalMin("0.1") Double radius) {

        List<NearbyShopResponse> nearbyShops = shopService.findNearbyShops(lat, lng, radius);
        return ResponseEntity.ok(nearbyShops);
    }

    @GetMapping
    @Operation(summary = "Get all shops")
    public ResponseEntity<List<Shop>> getAllShops() {
        return ResponseEntity.ok(shopService.getAllShops());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get shop by ID")
    public ResponseEntity<Shop> getShopById(@PathVariable Long id) {
        return ResponseEntity.ok(shopService.getShopById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new shop")
    public ResponseEntity<Shop> createShop(@Valid @RequestBody ShopRequestDto request) {
        Shop createdShop = shopService.createShop(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdShop);
    }

    @PostMapping("/bulk")
    @Operation(summary = "Bulk import multiple shops")
    public ResponseEntity<List<Shop>> createShopsBulk(@Valid @RequestBody List<@Valid ShopRequestDto> requests) {
        List<Shop> createdShops = shopService.createShopsBulk(requests);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdShops);
    }
}
