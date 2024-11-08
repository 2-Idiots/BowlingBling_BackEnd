package com.capstone.bowlingbling.domain.club.dto.clubSchedule;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreatedByDto {
    private final Long userId;
    private final String userName;
}