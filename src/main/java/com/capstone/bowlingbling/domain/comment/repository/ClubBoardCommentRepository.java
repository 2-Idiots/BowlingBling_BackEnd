package com.capstone.bowlingbling.domain.comment.repository;

import com.capstone.bowlingbling.domain.club.domain.ClubBoard;
import com.capstone.bowlingbling.domain.comment.domain.ClubBoardComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClubBoardCommentRepository extends JpaRepository<ClubBoardComment, Long> {
    List<ClubBoardComment> findByClubBoardId(Long postId);

    List<ClubBoardComment> findByClubBoard(ClubBoard clubBoard);
}
