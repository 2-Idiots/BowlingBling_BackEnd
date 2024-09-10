package com.capstone.bowlingbling.domain.lessoninfo.domain;

import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.global.BaseEntity;
import com.capstone.bowlingbling.global.enums.BowlingStyle;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonInfo extends BaseEntity {

    private String title; // 제목
    private String introduction; // 소개
    private String teacherName;
    private String qualifications; // 자격사항
    private String careerHistory; // 이력사항

    private BowlingStyle program; // 볼링 스타일

    private String location; // 볼링장 위치
    private String operatingHours; // 운영시간

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member; // 강사 정보
}
