package com.capstone.bowlingbling.domain.lesson.repository;

import com.capstone.bowlingbling.domain.lesson.domain.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findAllByTeacherId(Long teacherId);
    List<Lesson> findAllByStudentId(Long studentId);
}
