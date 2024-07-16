package com.capstone.bowlingbling.domain.club.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class ClubResponseListDto {
    //TODO thumbnail 추가
    private Long id;
    private String clubname;
    private int memberCount;
    private String leaderNickname;
}