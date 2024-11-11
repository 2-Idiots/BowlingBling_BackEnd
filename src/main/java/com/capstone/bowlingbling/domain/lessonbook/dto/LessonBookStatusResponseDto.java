package com.capstone.bowlingbling.domain.lessonbook.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LessonBookStatusResponseDto {
    private Long id;               // 예약 ID
    private String status;         // 변경된 상태 (CONFIRMED 또는 CANCELLED)
    private String updatedAt;      // 상태 변경 시간
    private String message;        // 성공 메시지
}