package com.capstone.bowlingbling.domain.lesson.repository;

import com.capstone.bowlingbling.domain.lesson.domain.LessonRequest;
import com.capstone.bowlingbling.global.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRequestRepository extends JpaRepository<LessonRequest, Long> {
    List<LessonRequest> findAllByTeacherIdAndStatus(Long teacherId, RequestStatus status);
}
