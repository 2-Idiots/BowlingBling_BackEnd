package com.capstone.bowlingbling.domain.comment.repository;

import com.capstone.bowlingbling.domain.center.domain.Center;
import com.capstone.bowlingbling.domain.comment.domain.CenterComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CenterCommentRepository extends JpaRepository<CenterComment, Long> {
    Page<CenterComment> findByCenter(Center center, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE CenterComment lc SET lc.conmments = :comments WHERE lc.id = :commentId")
    void updateComment(@Param("commentId") Long commentId, @Param("comments") String comments);
}