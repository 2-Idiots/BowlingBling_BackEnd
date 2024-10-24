package com.capstone.bowlingbling.domain.comment.repository;

import com.capstone.bowlingbling.domain.center.domain.Center;
import com.capstone.bowlingbling.domain.comment.domain.CenterComment;
import com.capstone.bowlingbling.domain.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CenterCommentRepository extends JpaRepository<CenterComment, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE CenterComment lc SET lc.comments = COALESCE(:comments, lc.comments) WHERE lc.id = :commentId")
    void updateComment(@Param("commentId") Long commentId, @Param("comments") String comments);

    Page<CenterComment> findByCenterAndDeletedAtIsNull(Center center, Pageable pageable);

    @Query("SELECT cc FROM CenterComment cc JOIN cc.center c WHERE cc.member = :member AND cc.deletedAt IS NULL")
    List<CenterComment> findByMemberAndDeletedAtIsNull(@Param("member") Member member);
}