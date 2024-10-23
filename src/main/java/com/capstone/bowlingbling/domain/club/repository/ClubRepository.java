package com.capstone.bowlingbling.domain.club.repository;

import com.capstone.bowlingbling.domain.club.domain.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClubRepository extends JpaRepository<Club, Long> {

    @Modifying
    @Query("UPDATE Club c SET " +
            "c.clubname = COALESCE(:clubname, c.clubname), " +
            "c.introduction = COALESCE(:introduction, c.introduction) " +
            "WHERE c.id = :id")
    void updateClubInfo(@Param("id") Long id, @Param("clubname") String clubname, @Param("introduction") String introduction);

}