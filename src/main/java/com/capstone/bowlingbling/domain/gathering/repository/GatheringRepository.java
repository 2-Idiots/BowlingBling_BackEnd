package com.capstone.bowlingbling.domain.gathering.repository;

import com.capstone.bowlingbling.domain.gathering.domain.Gathering;
import com.capstone.bowlingbling.domain.member.domain.Member;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GatheringRepository extends JpaRepository<Gathering, Long> {
    @Query("SELECT g FROM Gathering g WHERE g.deletedAt IS NULL ORDER BY g.createdAt DESC")
    Page<Gathering> findAllActive(Pageable pageable);

    Optional<Gathering> findById(Long Id);

    @Query("SELECT g FROM Gathering g JOIN g.memberGatherings mg JOIN mg.member m WHERE m.id = :memberId AND g.deletedAt IS NULL")
    Page<Gathering> findByMember(@Param("memberId") Long memberId, Pageable pageable);

    List<Gathering> findAllByDateBeforeAndDeletedAtIsNull(LocalDateTime date);

    @Modifying
    @Transactional
    @Query("UPDATE Gathering g SET " +
            "g.title = COALESCE(:title, g.title), " +
            "g.minAverage = COALESCE(:minAverage, g.minAverage), " +
            "g.maxAverage = COALESCE(:maxAverage, g.maxAverage), " +
            "g.description = COALESCE(:description, g.description), " +
            "g.location = COALESCE(:location, g.location), " +
            "g.date = COALESCE(:date, g.date), " +
            "g.maxParticipants = COALESCE(:maxParticipants, g.maxParticipants), " +
            "g.lat = COALESCE(:lat, g.lat), " +
            "g.lng = COALESCE(:lng, g.lng) " +
            "WHERE g.id = :id")
    void updateGathering(Long id, String title, Integer minAverage, Integer maxAverage, String description,
                         String location, LocalDate date, Integer maxParticipants, String lat, String lng);
}