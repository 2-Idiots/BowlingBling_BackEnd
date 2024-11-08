package com.capstone.bowlingbling.domain.club.dto.clubSchedule;

import com.capstone.bowlingbling.global.enums.ParticipationStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ParticipationRequestDto {
    private ParticipationStatus status;
    private String comment;
}