package com.capstone.bowlingbling.domain.club.repository;

import com.capstone.bowlingbling.domain.club.domain.Club;
import com.capstone.bowlingbling.domain.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClubRepository extends JpaRepository<Club, Long> {
    Page<Club> findAllByDeletedAtIsNull(Pageable pageable);

    @Query("SELECT c.members FROM Club c WHERE c.id = :clubId")
    List<Member> findMembersByClubId(@Param("clubId") Long clubId);

    @Query("SELECT c FROM Club c " +
            "JOIN c.members m " +
            "WHERE m.id = :memberId AND (m.clubRole = com.capstone.bowlingbling.global.enums.ClubRole.LEADER " +
            "OR m.clubRole = com.capstone.bowlingbling.global.enums.ClubRole.MANAGER)")
    List<Club> findManagingClubsByMemberId(@Param("memberId") Long memberId);

}