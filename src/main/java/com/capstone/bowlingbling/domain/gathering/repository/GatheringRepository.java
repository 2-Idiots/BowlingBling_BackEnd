package com.capstone.bowlingbling.domain.gathering.repository;

import com.capstone.bowlingbling.domain.gathering.domain.Gathering;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GatheringRepository extends JpaRepository<Gathering, Long> {
    @Query("SELECT g FROM Gathering g WHERE g.deletedAt IS NULL")
    Page<Gathering> findAllActive(Pageable pageable);

    @Query("SELECT g FROM Gathering g WHERE g.deletedAt IS NULL AND g.id = :id")
    Gathering findActiveById(@Param("id") Long id);

    @Query("SELECT g FROM Gathering g JOIN g.memberGatherings mg JOIN mg.member m WHERE m.id = :memberId AND g.deletedAt IS NULL")
    Page<Gathering> findByMember(@Param("memberId") Long memberId, Pageable pageable);

    @Query("UPDATE Gathering g SET g.deletedAt = :now WHERE g.date < :now AND g.deletedAt IS NULL")
    void markOldGatheringsAsDeleted(@Param("now") LocalDateTime now);

    List<Gathering> findAllByDateBeforeAndDeletedAtIsNull(LocalDateTime date);
}