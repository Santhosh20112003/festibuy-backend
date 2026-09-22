package com.festibuy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.festibuy.dto.NearbyShopProjection;
import com.festibuy.entity.Shop;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {

    @Query(value = """
        SELECT 
            s.id AS id,
            s.name AS name,
            s.address AS address,
            s.latitude AS latitude,
            s.longitude AS longitude,
            (ST_Distance(
                ST_SetSRID(ST_MakePoint(s.longitude, s.latitude), 4326)::geography,
                ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography
            ) / 1000.0) AS distanceKm
        FROM shops s
        WHERE ST_DWithin(
            ST_SetSRID(ST_MakePoint(s.longitude, s.latitude), 4326)::geography,
            ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography,
            :radiusMeters
        )
        ORDER BY distanceKm ASC
        """, nativeQuery = true)
    List<NearbyShopProjection> findNearbyShops(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radiusMeters") double radiusMeters);
}
