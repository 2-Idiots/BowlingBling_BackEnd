package com.capstone.bowlingbling.domain.club.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubDetailResponseDto {
    private Long id;
    private String name;
    private String description;
    private String location;
    private Integer memberCount;
    private Integer maxMembers;
    private Integer averageScore;
    private List<String> meetingDays;
    private List<String> images;
    private ClubMemberResponseDto leader;
    private Boolean isRecruiting;
    private String category;
    private String requirements;
    private Integer monthlyFee;
    private String establishedAt;
}