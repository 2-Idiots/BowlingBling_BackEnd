package com.capstone.bowlingbling.domain.club.repository;

import com.capstone.bowlingbling.domain.club.domain.Club;
import com.capstone.bowlingbling.domain.club.domain.ClubJoinList;
import com.capstone.bowlingbling.domain.club.dto.request.ClubJoinRequestDto;
import com.capstone.bowlingbling.global.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface ClubJoinListRepository extends JpaRepository<ClubJoinList, Long> {
    ClubJoinList findByClubAndStatus(Club club, RequestStatus status);
    Optional<ClubJoinList> findByClubAndMember(Club club, Long memberId);

    @Modifying
    @Transactional
    @Query("UPDATE ClubJoinList c SET c.status = :status WHERE c.id = :id")
    int updateJoinRequestStatus(@Param("id") Long id, @Param("status") ClubJoinRequestDto status);
}