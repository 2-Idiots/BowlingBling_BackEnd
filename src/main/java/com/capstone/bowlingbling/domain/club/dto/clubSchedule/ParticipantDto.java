package com.capstone.bowlingbling.domain.club.dto.clubSchedule;

import com.capstone.bowlingbling.global.enums.ParticipationStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ParticipantDto {
    private final Long userId;
    private final String userName;
    private final String userImage;
    private final ParticipationStatus status;
    private final String responseDate;
    private final String comment;
}
