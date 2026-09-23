package com.festibuy.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/shops")
@RequiredArgsConstructor
@Validated
@Tag(name = "Shops", description = "Shop and Geospatial Location APIs")
public class ShopController {

    private final ShopService shopService;

    @GetMapping("/nearby")
    @Operation(summary = "Find nearby shops within given radius using PostGIS")
    @RateLimiter(name = "shopApi")
    public ResponseEntity<List<NearbyShopResponse>> getNearbyShops(
            @RequestParam @NotNull(message = "Latitude is required")
            @DecimalMin("-90.0") @DecimalMax("90.0") Double lat,
            
            @RequestParam @NotNull(message = "Longitude is required")
            @DecimalMin("-180.0") @DecimalMax("180.0") Double lng,
            
            @RequestParam(defaultValue = "5.0")
            @DecimalMin(value = "0.1", message = "Radius must be at least 0.1 km")
            @DecimalMax(value = "50.0", message = "Radius cannot exceed 50.0 km") Double radius) {

        List<NearbyShopResponse> nearbyShops = shopService.findNearbyShops(lat, lng, radius);
        return ResponseEntity.ok(nearbyShops);
    }

    @GetMapping
    @Operation(summary = "Get all shops")
    @RateLimiter(name = "shopApi")
    public ResponseEntity<List<Shop>> getAllShops() {
        return ResponseEntity.ok(shopService.getAllShops());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get shop by ID")
    @RateLimiter(name = "shopApi")
    public ResponseEntity<Shop> getShopById(@PathVariable Long id) {
        return ResponseEntity.ok(shopService.getShopById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new shop")
    @RateLimiter(name = "shopWriteApi")
    public ResponseEntity<Shop> createShop(@Valid @RequestBody ShopRequestDto request) {
        Shop createdShop = shopService.createShop(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdShop);
    }

    @PostMapping("/bulk")
    @Operation(summary = "Bulk import multiple shops")
    @RateLimiter(name = "shopWriteApi")
    public ResponseEntity<List<Shop>> createShopsBulk(
            @RequestBody
            @NotEmpty(message = "Shop list cannot be empty")
            @Size(min = 1, max = 100, message = "Bulk import limited to maximum 100 shops per request")
            List<@Valid ShopRequestDto> requests) {
        List<Shop> createdShops = shopService.createShopsBulk(requests);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdShops);
    }
}
