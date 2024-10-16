package com.capstone.bowlingbling.domain.lesson.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingLessonResponseDto {
    private Long id;
    private Long studentId;
    private String requestMessage;
    private LocalDateTime requestDate;
}
