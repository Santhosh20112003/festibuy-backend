package com.festibuy.dto;

public interface NearbyShopProjection {
    Long getId();
    String getName();
    String getAddress();
    Double getLatitude();
    Double getLongitude();
    Double getDistanceKm();
}
