package com.capstone.bowlingbling.domain.lesson.dto.response;

import com.capstone.bowlingbling.global.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonResponseDto {
    private Long id;
    private Long teacherId;
    private Long studentId;
    private String requestMessage;
    private LocalDateTime requestDate;
    private RequestStatus status;
}