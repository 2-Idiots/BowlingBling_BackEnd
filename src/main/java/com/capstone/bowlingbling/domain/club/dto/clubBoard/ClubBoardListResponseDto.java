package com.capstone.bowlingbling.domain.club.dto.clubBoard;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ClubBoardListResponseDto {
    private final List<ClubBoardDetailDto> posts;
    private final Long totalElements;
    private final Integer totalPages;
    private final Integer size;
    private final Integer number;
}