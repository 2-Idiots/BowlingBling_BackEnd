package com.capstone.bowlingbling.domain.member.controller;

import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.domain.member.dto.MemberProfileUpdateRequest;
import com.capstone.bowlingbling.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @Operation(summary = "프로필 수정", description = "회원 프로필을 수정합니다.")
    @PutMapping("/profile/update")
    public ResponseEntity<Member> updateProfile(@RequestBody MemberProfileUpdateRequest request,
            @AuthenticationPrincipal User sessionMember) {

        String memberEmail = sessionMember.getUsername();
        Member updatedMember = memberService.updateProfile(request, memberEmail);
        return ResponseEntity.ok(updatedMember);
    }
}
