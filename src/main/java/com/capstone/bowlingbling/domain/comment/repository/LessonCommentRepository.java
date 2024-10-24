package com.capstone.bowlingbling.domain.comment.repository;

import com.capstone.bowlingbling.domain.comment.domain.LessonComment;
import com.capstone.bowlingbling.domain.lessoninfo.domain.LessonInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface LessonCommentRepository extends JpaRepository<LessonComment, Long> {
    Page<LessonComment> findByLesson(LessonInfo lessonInfo, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE LessonComment lc SET lc.comments = COALESCE(:comments, lc.comments) WHERE lc.id = :commentId")
    void updateComment(@Param("commentId") Long commentId, @Param("comments") String comments);

    Page<LessonComment> findByLessonAndDeletedAtIsNull(LessonInfo lessonInfo, Pageable pageable);
}