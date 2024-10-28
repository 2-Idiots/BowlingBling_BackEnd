package com.capstone.bowlingbling.domain.lessonbook.dto;

import com.capstone.bowlingbling.global.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonBookStatusDto {
    private Long requestId;
    private RequestStatus status;
}
