package com.capstone.bowlingbling.domain.lessonrequests.dto;

import com.capstone.bowlingbling.global.enums.Days;
import com.capstone.bowlingbling.global.enums.RequestStatus;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LessonBookedStudentListDto {
    private String studentId;
    private String studentName;
    private String date;
    private String time;
    private RequestStatus accepted;
}