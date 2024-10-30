package com.capstone.bowlingbling.domain.club.dto.response;

import com.capstone.bowlingbling.domain.member.domain.Member;
import lombok.Getter;

@Getter
public class ClubMemberResponseDto {
    private Long id;
    private String email;
    private String name;
    private String nickname;
    private String image;

    public ClubMemberResponseDto(Member leader) {
        this.id = leader.getId();
        this.email = leader.getEmail();
        this.name = leader.getName();
        this.nickname = leader.getNickname();
        this.image = leader.getImage();
    }
}
