package com.capstone.bowlingbling.domain.lessonbook.dto;

import com.capstone.bowlingbling.global.enums.RequestStatus;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LessonBookedStudentDto {
    private Long id;                    // 예약 ID
    private Long lessonId;              // 레슨 ID
    private String studentName;         // 수강생 이름
    private String date;                // 예약 날짜 (YYYY-MM-DD)
    private String time;                // 예약 시간 (HH:mm)
    private RequestStatus status;       // 예약 상태
    private Integer price;              // 레슨 가격
    private String createdAt;           // 예약 생성 시간
}