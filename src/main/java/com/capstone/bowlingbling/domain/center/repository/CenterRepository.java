package com.capstone.bowlingbling.domain.center.repository;

import com.capstone.bowlingbling.domain.center.domain.Center;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CenterRepository extends JpaRepository<Center, Long> {
}

