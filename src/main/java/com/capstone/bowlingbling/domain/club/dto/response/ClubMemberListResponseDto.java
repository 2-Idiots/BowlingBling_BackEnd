package com.capstone.bowlingbling.domain.club.dto.response;

import com.capstone.bowlingbling.global.enums.ClubRole;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ClubMemberListResponseDto {
    private ClubMemberResponseDto members;
    private ClubRole role;
    private String joinedAt;
    private Integer averageScore;
}
