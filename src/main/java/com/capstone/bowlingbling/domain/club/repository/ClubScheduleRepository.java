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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClubScheduleRepository extends JpaRepository<ClubSchedule, Long> {
    @Query("SELECT cs FROM ClubSchedule cs WHERE cs.club.id = :clubId " +
            "AND SUBSTRING(cs.startDate, 1, 4) = :year " + // 년 추출
            "AND SUBSTRING(cs.startDate, 6, 2) = :month") // 월 추출
    List<ClubSchedule> findSchedulesByMonth(@Param("clubId") Long clubId,
                                            @Param("year") String year,
                                            @Param("month") String month);

    @Modifying
    @Query("UPDATE ClubSchedule cs SET " +
            "cs.title = COALESCE(:title, cs.title), " +
            "cs.description = COALESCE(:description, cs.description), " +
            "cs.location = COALESCE(:location, cs.location), " +
            "cs.startDate = COALESCE(:startDate, cs.startDate), " +
            "cs.endDate = COALESCE(:endDate, cs.endDate), " +
            "cs.maxParticipants = COALESCE(:maxParticipants, cs.maxParticipants), " +
            "cs.deadlineDate = COALESCE(:deadlineDate, cs.deadlineDate), " +
            "cs.cancelableDate = COALESCE(:cancelableDate, cs.cancelableDate) " +
            "WHERE cs.id = :scheduleId")
    void updateSchedule(@Param("scheduleId") Long scheduleId,
                        @Param("title") String title,
                        @Param("description") String description,
                        @Param("location") String location,
                        @Param("startDate") String startDate,
                        @Param("endDate") String endDate,
                        @Param("maxParticipants") Integer maxParticipants,
                        @Param("deadlineDate") String deadlineDate,
                        @Param("cancelableDate") String cancelableDate);

    @Query("SELECT cs FROM ClubSchedule cs WHERE cs.id = :scheduleId AND cs.club.id = :clubId")
    Optional<ClubSchedule> findByIdAndClubId(@Param("scheduleId") Long scheduleId, @Param("clubId") Long clubId);

    @Modifying
    @Query("UPDATE ClubSchedule cs SET cs.deletedAt = :deletedAt WHERE cs.id = :scheduleId")
    void softDeleteSchedule(Long scheduleId, LocalDateTime deletedAt);
}