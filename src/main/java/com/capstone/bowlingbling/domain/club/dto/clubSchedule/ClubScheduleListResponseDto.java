package com.capstone.bowlingbling.domain.club.dto.clubSchedule;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ClubScheduleListResponseDto {
    private final Long id;
    private final String title;
    private final String description;
    private final String location;
    private final String startDate;
    private final String endDate;
    private final Integer maxParticipants;
    private final Integer currentParticipants;
    private final Boolean isRegular;
    private final String deadlineDate;
    private final String cancelableDate;
    private final List<ParticipantDto> participants;  // 참가자 정보
    private final CreatedByDto createdBy;  // 생성자 정보
    private final String createdAt;
    private final String updatedAt;
}
