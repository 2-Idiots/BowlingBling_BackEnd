package com.capstone.bowlingbling.domain.center.repository;

import com.capstone.bowlingbling.domain.center.domain.Center;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CenterRepository extends JpaRepository<Center, Long> {

    @Modifying
    @Query("UPDATE Center c SET c.businessName = :businessName, c.location = :location, c.operatingHours = :operatingHours, " +
            "c.announcements = :announcements, c.laneCount = :laneCount, c.images = :images WHERE c.id = :centerId")
    int updateCenter(@Param("centerId") Long centerId,
                     @Param("businessName") String businessName,
                     @Param("location") String location,
                     @Param("operatingHours") String operatingHours,
                     @Param("announcements") String announcements,
                     @Param("laneCount") Integer laneCount,
                     @Param("images") List<String> images);
}

