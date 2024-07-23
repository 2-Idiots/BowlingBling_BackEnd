package com.capstone.bowlingbling.domain.place.repository;

import com.capstone.bowlingbling.domain.place.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, Long> {
}
