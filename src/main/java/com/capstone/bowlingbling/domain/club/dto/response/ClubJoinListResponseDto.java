package com.capstone.bowlingbling.domain.club.dto.response;

import com.capstone.bowlingbling.global.enums.RequestStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ClubJoinListResponseDto {
    private Long id;
    private Long userId;
    private Long clubId;
    private int averageScore;
    private String experience;
    private String motivation;
    private List<Boolean> availability;
    private RequestStatus status;
    private LocalDateTime createdAt;
    private ClubMemberResponseDto user;
}
