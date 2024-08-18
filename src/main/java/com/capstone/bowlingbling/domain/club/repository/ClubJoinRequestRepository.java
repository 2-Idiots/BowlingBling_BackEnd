package com.capstone.bowlingbling.domain.club.repository;

import com.capstone.bowlingbling.domain.club.domain.Club;
import com.capstone.bowlingbling.domain.club.domain.ClubJoinRequest;
import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.global.enums.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClubJoinRequestRepository extends JpaRepository<ClubJoinRequest, Long> {
    Page<ClubJoinRequest> findAllByClubAndStatus(Club club, RequestStatus status, Pageable pageable);
    Optional<ClubJoinRequest> findByClubAndMember(Club club, Member member);
}