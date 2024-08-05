package com.capstone.bowlingbling.domain.place.repository;

import com.capstone.bowlingbling.domain.place.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    Optional<Place> findById(Long id);
}
