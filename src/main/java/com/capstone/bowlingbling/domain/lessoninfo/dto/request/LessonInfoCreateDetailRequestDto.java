package com.capstone.bowlingbling.domain.lessoninfo.dto.request;

import com.capstone.bowlingbling.global.enums.BowlingStyle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonInfoCreateDetailRequestDto {
    private String title;
    private String teacherName;
    private String introduction;
    private String qualifications;
    private String careerHistory;
    private BowlingStyle program;
    private String location;
    private String operatingHours;
    private String contents;
}