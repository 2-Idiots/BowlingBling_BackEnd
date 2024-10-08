package com.capstone.bowlingbling.global.auth.signup.controller;

import com.capstone.bowlingbling.global.auth.signup.dto.MemberSignUpDto;
import com.capstone.bowlingbling.global.auth.signup.service.SignUpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "회원가입", description = "Sign-Up API")
public class SignUpController {

    private final SignUpService memberService;

    @PostMapping("/sign-up")
    @Operation(summary = "회원가입", description = "회원가입에 필요한 정보를 받아 가입을 진행합니다.")
    public String signUp(@RequestBody MemberSignUpDto memberSignUpDto) throws Exception {
        memberService.signUp(memberSignUpDto);
        return "회원가입 성공";
    }
}