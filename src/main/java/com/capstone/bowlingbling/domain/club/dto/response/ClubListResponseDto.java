package com.capstone.bowlingbling.domain.club.dto.response;


import com.capstone.bowlingbling.global.enums.ClubTags;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubListResponseDto {
    private Long id;
    private String clubname;
    private String introduction;
    private String leaderName;
    private String thumbNail;
    private int MaxCount;
    private int MinAverage;
    private int dues;
    private String address;
    private ClubTags clubTags;
}
