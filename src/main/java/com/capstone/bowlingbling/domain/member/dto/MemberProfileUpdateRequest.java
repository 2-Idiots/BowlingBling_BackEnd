package com.capstone.bowlingbling.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberProfileUpdateRequest {
    private String nickname;
    private String imageUrl;
    private String phonenum;
    private String city;
    private String sex;
    private Integer age; // Integer를 사용하여 null을 허용
}