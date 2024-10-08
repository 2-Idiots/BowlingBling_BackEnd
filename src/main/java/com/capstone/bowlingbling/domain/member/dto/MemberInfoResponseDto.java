package com.capstone.bowlingbling.domain.member.dto;

import com.capstone.bowlingbling.global.enums.Role;
import com.capstone.bowlingbling.global.enums.SocialType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class MemberInfoResponseDto {
    private String email;
    private String name;
    private String nickname;
    private String city;
    private Integer age;
    private String phonenum;
    private String image;
    private String introduction;
    private String sex;
    private Role role;
    private SocialType socialType;
}