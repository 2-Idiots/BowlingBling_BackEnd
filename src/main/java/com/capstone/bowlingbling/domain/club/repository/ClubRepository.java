package com.capstone.bowlingbling.domain.club.repository;

import com.capstone.bowlingbling.domain.club.domain.Club;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ClubRepository extends JpaRepository<Club, Long> {

    Page<Club> findAllByDeletedAtIsNull(Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Club c SET " +
            "c.clubName = COALESCE(:name, c.clubName), " +
            "c.description = COALESCE(:description, c.description), " +
            "c.location = COALESCE(:location, c.location), " +
            "c.maxMembers = COALESCE(:maxMembers, c.maxMembers), " +
            "c.category = COALESCE(:category, c.category), " +
            "c.requirements = COALESCE(:requirements, c.requirements), " +
            "c.monthlyFee = COALESCE(:monthlyFee, c.monthlyFee), " +
            "c.averageScore = COALESCE(:averageScore, c.averageScore) " +
            "WHERE c.id = :clubId")
    void updateClubSettings(Long clubId, String name, String description, String location, Integer maxMembers,
                                 String category, String requirements, Integer monthlyFee, Integer averageScore);

    @Modifying
    @Query("UPDATE Club c SET c.isRecruiting = :isRecruiting WHERE c.id = :clubId")
    void updateRecruitmentStatus(@Param("clubId") Long clubId, @Param("isRecruiting") Boolean isRecruiting);
}