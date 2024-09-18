package com.capstone.bowlingbling.domain.lessoninfo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonInfoListRequestDto {
    private String title;
    private String teacherName;
    private String introduction;
    private String location;
    private String imageUrl;
}
