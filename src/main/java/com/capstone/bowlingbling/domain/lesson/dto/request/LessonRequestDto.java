package com.capstone.bowlingbling.domain.lesson.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonRequestDto {
    private Long teacherId;
    private String requestMessage;
}
