package com.capstone.bowlingbling.domain.club.dto.clubSchedule;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClubScheduleUpdateRequestDto {
    private final String title;
    private final String description;
    private final String location;
    private final String startDate;
    private final String endDate;
    private final Integer maxParticipants;
    private final String deadlineDate;
    private final String cancelableDate;
}
