package com.capstone.bowlingbling.domain.center.repository;

import com.capstone.bowlingbling.domain.center.domain.Center;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CenterRepository extends JpaRepository<Center, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE Center c SET " +
            "c.businessName = COALESCE(:businessName, c.businessName), " +
            "c.location = COALESCE(:location, c.location), " +
            "c.operatingHours = COALESCE(:operatingHours, c.operatingHours), " +
            "c.announcements = COALESCE(:announcements, c.announcements), " +
            "c.laneCount = COALESCE(:laneCount, c.laneCount), " +
            "c.lat = COALESCE(:lat, c.lat), " +
            "c.lng = COALESCE(:lng, c.lng) " +
            "WHERE c.id = :centerId")
    void updateCenter(Long centerId, String businessName, String location, String operatingHours,
                      String announcements, Integer laneCount, String lat, String lng);
}

