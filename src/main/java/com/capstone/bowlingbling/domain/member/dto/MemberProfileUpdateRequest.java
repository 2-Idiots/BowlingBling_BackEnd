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
    private String email; // 이메일
    private String name;
    private String nickname;
    private String phonenum;
    private String city;
    private String sex;
    private String introduction;
    private Integer myaver;
    private Integer age; // Integer를 사용하여 null을 허용
}