package com.capstone.bowlingbling.domain.club.dto.response;

import com.capstone.bowlingbling.global.enums.ClubRole;
import com.capstone.bowlingbling.global.enums.RequestStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ClubMemberDetailResponseDto {
    private ClubMemberResponseDto members;
    private ClubRole role;
    private String joinedAt;
    private Integer averageScore;
    private RequestStatus status;
    private Integer attendanceRate;
    private String lastAttendance;
    private List<Integer> recentScores;
}

