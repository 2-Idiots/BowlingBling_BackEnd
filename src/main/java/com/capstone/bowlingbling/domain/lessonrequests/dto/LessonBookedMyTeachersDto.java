package com.capstone.bowlingbling.domain.lessonrequests.dto;

import com.capstone.bowlingbling.global.enums.Days;
import com.capstone.bowlingbling.global.enums.RequestStatus;
import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LessonBookedMyTeachersDto {

    private Long id;
    private String teacherName;
    private String date;
    private String time;
    private RequestStatus status;
    private Integer price;
}
