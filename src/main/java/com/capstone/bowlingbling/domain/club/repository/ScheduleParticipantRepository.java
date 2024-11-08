package com.capstone.bowlingbling.domain.club.repository;

import com.capstone.bowlingbling.domain.club.domain.ClubSchedule;
import com.capstone.bowlingbling.domain.club.domain.ScheduleParticipant;
import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.global.enums.ParticipationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleParticipantRepository extends JpaRepository<ScheduleParticipant, Long> {

    ScheduleParticipant findByClubScheduleAndMember(ClubSchedule clubSchedule, Member member);

    // 참석 상태를 업데이트하는 쿼리 메서드
    @Modifying
    @Query("UPDATE ScheduleParticipant sp SET sp.status = :status, sp.comment = :comment, sp.responseDate = :responseDate WHERE sp.id = :participantId")
    void updateParticipationStatus(@Param("participantId") Long participantId,
                                   @Param("status") ParticipationStatus status,
                                   @Param("comment") String comment,
                                   @Param("responseDate") String responseDate);

    List<ScheduleParticipant> findByClubScheduleIdAndStatus(Long scheduleId, ParticipationStatus status);

    @Query("SELECT COUNT(sp) FROM ScheduleParticipant sp " +
            "WHERE sp.clubSchedule.id = :scheduleId AND sp.status = :status")
    int countByStatus(Long scheduleId, ParticipationStatus status);
}