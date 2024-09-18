package com.capstone.bowlingbling.domain.lessoninfo.dto.response;

import com.capstone.bowlingbling.global.enums.BowlingStyle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonInfoResponseDto {
    private String title;
    private String teacherName;
    private String introduction;
    private String location;
    private String qualifications;
    private String careerHistory;
    private BowlingStyle program;
    private String operatingHours;
    private List<String> imageUrls;
}