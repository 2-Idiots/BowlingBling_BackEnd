package com.capstone.bowlingbling.domain.lessoninfo.repository;

import com.capstone.bowlingbling.domain.lessoninfo.domain.LessonInfo;
import com.capstone.bowlingbling.domain.lessoninfo.domain.LikedLesson;
import com.capstone.bowlingbling.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikedLessonRepository extends JpaRepository<LikedLesson, Long> {

    boolean existsByMemberAndLessonInfo(Member member, LessonInfo lessonInfo);

    Optional<LikedLesson> findByMemberAndLessonInfo(Member member, LessonInfo lessonInfo);

    List<LikedLesson> findByMember(Member member);
}
