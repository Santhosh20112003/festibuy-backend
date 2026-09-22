package com.festibuy.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NearbyShopResponse {

    private Long id;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private Double distanceKm;

    public static NearbyShopResponse fromProjection(NearbyShopProjection projection) {
        Double rawDistance = projection.getDistanceKm();
        Double roundedDistance = rawDistance != null ? Math.round(rawDistance * 100.0) / 100.0 : null;
        return NearbyShopResponse.builder()
                .id(projection.getId())
                .name(projection.getName())
                .address(projection.getAddress())
                .latitude(projection.getLatitude())
                .longitude(projection.getLongitude())
                .distanceKm(roundedDistance)
                .build();
    }
}
