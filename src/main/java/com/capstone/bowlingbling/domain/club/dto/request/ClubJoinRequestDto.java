package com.capstone.bowlingbling.domain.club.dto.request;

import com.capstone.bowlingbling.global.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClubJoinRequestDto {
    private Long memberId;
    private RequestStatus Status;
}