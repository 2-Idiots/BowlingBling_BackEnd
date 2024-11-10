package com.capstone.bowlingbling.domain.club.dto.clubBoard;

import com.capstone.bowlingbling.global.enums.ClubCategory;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClubBoardCreateDto {
    private final String title;
    private final String content;
    private final ClubCategory category;
    private final Boolean isPinned;
}
