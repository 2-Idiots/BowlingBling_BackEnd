package com.capstone.bowlingbling.domain.club.dto.request;

import com.capstone.bowlingbling.global.enums.RequestStatus;
import lombok.Getter;

@Getter
public class ClubMemberStatusUpdateDto {
    private RequestStatus status;
    private String reason;
}
