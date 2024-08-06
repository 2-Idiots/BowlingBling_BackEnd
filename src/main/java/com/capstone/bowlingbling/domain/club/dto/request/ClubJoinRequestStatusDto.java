package com.capstone.bowlingbling.domain.club.dto.request;

import com.capstone.bowlingbling.global.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class ClubJoinRequestStatusDto {
    private RequestStatus status;
}