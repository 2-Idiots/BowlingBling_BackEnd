package com.capstone.bowlingbling.domain.club.repository;

import com.capstone.bowlingbling.domain.club.domain.ClubJoinList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClubJoinRequestRepository extends JpaRepository<ClubJoinList, Long> {
}