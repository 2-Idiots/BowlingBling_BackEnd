package com.capstone.bowlingbling.domain.club.dto.clubSchedule;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MonthlySchedulesResponseDto {
    private final List<ClubScheduleListResponseDto> schedules;
    private final Integer totalCount;
}