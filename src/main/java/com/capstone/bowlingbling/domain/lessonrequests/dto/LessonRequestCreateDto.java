package com.capstone.bowlingbling.domain.lessonrequests.dto;


import com.capstone.bowlingbling.global.enums.Days;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonRequestCreateDto {

    private Long lessonId;
    private Days dayOfWeek;
    private String time;
}