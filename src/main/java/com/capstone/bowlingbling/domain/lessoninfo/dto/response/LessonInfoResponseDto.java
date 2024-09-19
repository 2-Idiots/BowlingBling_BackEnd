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
    private String contents;
    private String location;
    private String qualifications;
    private String lat;
    private String lng;
    private String place;
    private String category;
    private Integer price;
    private Boolean hasFreeParking;
    private String careerHistory;
    private BowlingStyle program;
    private String operatingHours;
    private List<String> imageUrls;
}