package com.capstone.bowlingbling.domain.lessonrequests.domain;

import com.capstone.bowlingbling.domain.lessoninfo.domain.LessonInfo;
import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.global.BaseEntity;
import com.capstone.bowlingbling.global.enums.Days;
import com.capstone.bowlingbling.global.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonRequest extends BaseEntity {

    @ManyToOne
    private Member student;

    @ManyToOne
    private Member teacher;

    @ManyToOne
    private LessonInfo lessonInfo;

    private Days dayOfWeek;  // 요일 (e.g., "Monday", "Tuesday")

    private String time;  // 시간 (e.g., "10:00", "14:00")

    @Enumerated(EnumType.STRING)
    private RequestStatus status;
}