package com.capstone.bowlingbling.domain.club.repository;

import com.capstone.bowlingbling.domain.club.domain.ClubJoinList;
import com.capstone.bowlingbling.global.enums.ClubRole;
import com.capstone.bowlingbling.global.enums.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ClubJoinListRepository extends JpaRepository<ClubJoinList, Long> {
    Page<ClubJoinList> findByClubIdAndStatus(Long clubId, RequestStatus status, Pageable pageable);

    List<ClubJoinList> findByClub_Id(Long clubId);

    Optional<ClubJoinList> findByClub_IdAndMember_Id(Long clubId, Long memberId);

    @Modifying
    @Transactional
    @Query("UPDATE ClubJoinList c SET c.status = :status WHERE c.id = :requestId")
    void updateJoinRequestStatus(@Param("requestId") Long requestId, @Param("status") RequestStatus status);

    @Modifying
    @Query("UPDATE ClubJoinList cj SET cj.clubRole = :role WHERE cj.club.id = :clubId AND cj.member.id = :memberId")
    void updateClubRole(@Param("clubId") Long clubId, @Param("memberId") Long memberId, @Param("role") ClubRole role);

    @Query("SELECT cj FROM ClubJoinList cj WHERE cj.member.email = :email AND cj.status = 'ACCEPTED'")
    List<ClubJoinList> findClubsByMemberEmail(@Param("email") String email);

    // 사용자가 매니징하는 클럽 (LEADER 또는 MANAGER 역할)
    @Query("SELECT cj FROM ClubJoinList cj WHERE cj.member.email = :email AND cj.clubRole IN (:roles)")
    List<ClubJoinList> findManagingClubsByMemberEmail(@Param("email") String email, @Param("roles") List<ClubRole> roles);

    // 신청 대기 중인 클럽 (PENDING 상태)
    @Query("SELECT cj FROM ClubJoinList cj WHERE cj.member.email = :email AND cj.status = 'PENDING'")
    List<ClubJoinList> findPendingApplicationsByMemberEmail(@Param("email") String email);
}