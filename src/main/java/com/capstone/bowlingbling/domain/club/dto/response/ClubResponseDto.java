package com.capstone.bowlingbling.domain.club.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class ClubResponseDto {
    //TODO place와 image 추가
    private Long id;
    private String clubname;
    private String introduction;
    private int memberCount;
    private String leaderNickname;
}