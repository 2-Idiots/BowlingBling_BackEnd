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

    public ClubMemberResponseDto(Member member) {
        this.id = member.getId();
        this.email = member.getEmail();
        this.name = member.getName();
        this.nickname = member.getNickname();
        this.image = member.getImage();
    }
}
