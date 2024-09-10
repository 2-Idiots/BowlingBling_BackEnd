package com.capstone.bowlingbling.domain.lessoninfo.repository;

import com.capstone.bowlingbling.domain.lessoninfo.domain.LessonInfo;
import com.capstone.bowlingbling.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonInfoRepository extends JpaRepository<LessonInfo, Long> {
    boolean existsByMember(Member member);
}
