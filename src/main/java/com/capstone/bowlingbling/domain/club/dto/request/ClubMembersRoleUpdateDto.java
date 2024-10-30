package com.capstone.bowlingbling.domain.club.dto.request;

import com.capstone.bowlingbling.global.enums.ClubRole;
import lombok.Getter;

@Getter
public class ClubMembersRoleUpdateDto {
    private ClubRole role;
}
