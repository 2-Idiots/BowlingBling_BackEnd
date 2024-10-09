package com.capstone.bowlingbling.domain.lessoninfo.domain;

import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.global.BaseEntity;
import com.capstone.bowlingbling.global.enums.BowlingStyle;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class LessonInfo extends BaseEntity {

    private String title; // 제목
    private String intro; // 소개
    private String contents; // 내용
    private String teacherName;
    private String qualifications; // 자격사항
    private String careerHistory; // 이력사항
    private String lat;
    private String lng;
    private String place;
    private String category;
    private Integer price;
    private Boolean hasFreeParking;

    private BowlingStyle program; // 볼링 스타일
    private String address; // 볼링장 위치
    private String operatingHours; // 운영시간

    @ElementCollection
    private List<String> images;

    @ManyToOne
    private Member member; // 강사 정보
}
