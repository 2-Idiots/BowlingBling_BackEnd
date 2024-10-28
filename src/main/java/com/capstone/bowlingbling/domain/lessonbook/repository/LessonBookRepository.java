package com.capstone.bowlingbling.domain.lessonbook.repository;

import com.capstone.bowlingbling.domain.lessonbook.domain.LessonBook;
import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.global.enums.RequestStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LessonBookRepository extends JpaRepository<LessonBook, Long> {

    List<LessonBook> findByLessonInfo_Id(Long lessonInfoId);

    @Query("SELECT lr FROM LessonBook lr WHERE lr.date = :date AND lr.time = :time AND lr.teacher.id = :teacherId")
    Optional<LessonBook> findByDayOfWeekAndTimeAndTeacher_Id(@Param("date") String date, @Param("time") String time, @Param("teacherId") Long teacherId);

    List<LessonBook> findByStudent(Member student);  // 학생이 신청한 요청 목록

    List<LessonBook> findByTeacher(Member teacher);  // 선생에게 온 요청 목록

    @Modifying
    @Transactional
    @Query("UPDATE LessonBook lr SET lr.status = :status WHERE lr.id = :requestId AND lr.teacher.email = :teacherEmail")
    int updateStatus(Long requestId, RequestStatus status, String teacherEmail);
}