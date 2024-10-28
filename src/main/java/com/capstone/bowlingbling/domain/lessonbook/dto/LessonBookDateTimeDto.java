package com.capstone.bowlingbling.domain.lessonbook.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LessonBookDateTimeDto {
    private String date;  // 예약된 날짜
    private String time;  // 예약된 시간
}