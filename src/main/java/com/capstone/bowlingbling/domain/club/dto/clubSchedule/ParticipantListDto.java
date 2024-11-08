package com.capstone.bowlingbling.domain.club.dto.clubSchedule;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ParticipantListDto {
    private final List<ParticipantDto> participant;
    private final Integer attending;
    private final Integer notAttending;
    private final Integer number;
}
