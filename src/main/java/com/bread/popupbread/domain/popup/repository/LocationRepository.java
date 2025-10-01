package com.bread.popupbread.domain.popup.repository;

import com.bread.popupbread.domain.popup.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Integer> {
    Optional<Location> findByRegionAndDistrict(String region, String district);
    Optional<Location> findByRegionAndDistrictIsNull(String region);
    boolean existsByRegion(String region);
    boolean existsByRegionAndDistrict(String region, String district);
}
