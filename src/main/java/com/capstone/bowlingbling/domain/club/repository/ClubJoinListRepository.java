package com.capstone.bowlingbling.domain.club.repository;

import com.capstone.bowlingbling.domain.club.domain.ClubJoinList;
import com.capstone.bowlingbling.global.enums.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ClubJoinListRepository extends JpaRepository<ClubJoinList, Long> {
    Page<ClubJoinList> findByClubIdAndStatus(Long clubId, RequestStatus status, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE ClubJoinList c SET c.status = :status WHERE c.id = :requestId")
    void updateJoinRequestStatus(@Param("requestId") Long requestId, @Param("status") RequestStatus status);
}