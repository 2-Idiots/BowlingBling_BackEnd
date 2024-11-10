package com.capstone.bowlingbling.domain.club.repository;

import com.capstone.bowlingbling.domain.club.domain.Club;
import com.capstone.bowlingbling.domain.club.domain.ClubBoard;
import com.capstone.bowlingbling.global.enums.ClubCategory;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClubBoardRepository extends JpaRepository<ClubBoard, Long>, JpaSpecificationExecutor<ClubBoard> {
    List<ClubBoard> findByClubAndIsPinnedTrue(Club club);

    @Modifying
    @Transactional
    @Query("UPDATE ClubBoard b SET " +
            "b.title = COALESCE(:title, b.title), " +
            "b.content = COALESCE(:content, b.content), " +
            "b.clubCategory = COALESCE(:category, b.clubCategory), " +
            "b.isPinned = COALESCE(:isPinned, b.isPinned) " +
            "WHERE b.id = :postId")
    void updatePost(Long postId, String title, String content, ClubCategory category, Boolean isPinned);
}
