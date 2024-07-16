package com.capstone.bowlingbling.global.auth.signup.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MemberSignUpDto {

    private String email;
    private String password;
    private String nickname;
    private int age;
    private String city;
}