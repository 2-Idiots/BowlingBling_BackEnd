package com.capstone.bowlingbling.domain.member.controller;

import com.capstone.bowlingbling.domain.lessoninfo.dto.response.LessonInfoResponseDto;
import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.domain.member.domain.TeacherRequest;
import com.capstone.bowlingbling.domain.member.dto.MemberInfoResponseDto;
import com.capstone.bowlingbling.domain.member.dto.MemberProfileUpdateRequest;
import com.capstone.bowlingbling.domain.member.dto.TeacherRequestDto;
import com.capstone.bowlingbling.domain.member.service.MemberService;
import com.capstone.bowlingbling.global.enums.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/users")
@Tag(name = "users", description = "회원 프로필 및 권한 요청 API")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @Operation(summary = "유저 정보 조회", description = "현재 로그인된 유저의 정보를 조회합니다.")
    @GetMapping("/info")
    public ResponseEntity<MemberInfoResponseDto> getUserInfo(@AuthenticationPrincipal User sessionMember) {
        String memberEmail = sessionMember.getUsername();
        MemberInfoResponseDto memberInfo = memberService.getMemberInfo(memberEmail);
        return ResponseEntity.ok(memberInfo);
    }

    @Operation(summary = "프로필 수정", description = "회원 프로필을 수정합니다.")
    @PatchMapping(value = "/profile/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateProfile(@RequestPart(value = "request") MemberProfileUpdateRequest request,
                                                @Parameter(description = "업로드할 파일 목록", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
                                                @RequestPart(value = "files", required = false) MultipartFile file,
                                                @AuthenticationPrincipal User sessionMember) throws IOException {

        String memberEmail = sessionMember.getUsername();
        memberService.updateProfile(request, memberEmail, file);
        return ResponseEntity.ok("수정 완료");
    }

    @Operation(summary = "Teacher 권한 요청", description = "Teacher 권한을 요청합니다.")
    @PostMapping("/request-teacher")
    public ResponseEntity<TeacherRequest> requestTeacher(@RequestBody TeacherRequestDto request,
                                                         @AuthenticationPrincipal User sessionMember) {

        String memberEmail = sessionMember.getUsername();
        TeacherRequest teacherRequest = memberService.requestTeacherRole(request, memberEmail);
        return ResponseEntity.ok(teacherRequest);
    }

    @Operation(summary = "대기 중인 Teacher 요청 목록", description = "대기 중인 Teacher 요청 목록을 조회합니다.")
    @GetMapping("/teacher-requests")
    public ResponseEntity<Page<TeacherRequest>> getPendingTeacherRequests(Pageable pageable) {
        Page<TeacherRequest> teacherRequests = memberService.getPendingTeacherRequests(pageable);
        return ResponseEntity.ok(teacherRequests);
    }

    @Operation(summary = "Teacher 요청 승인", description = "Teacher 요청을 승인합니다.")
    @PostMapping("/approve-teacher/{requestId}")
    public ResponseEntity<Void> approveTeacherRequest(@PathVariable Long requestId,
                                                      @AuthenticationPrincipal User sessionMember) {

        String adminEmail = sessionMember.getUsername();
        Member adminMember = memberService.findByEmail(adminEmail);
        if (adminMember.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        memberService.approveTeacherRequest(requestId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/liked-lessons")
    @Operation(summary = "사용자가 찜한 레슨 목록 조회", description = "사용자가 찜한 레슨 목록을 조회합니다.")
    public ResponseEntity<List<LessonInfoResponseDto>> getLikedLessons(
            @AuthenticationPrincipal User sessionMember) {

        String userEmail = sessionMember.getUsername();
        List<LessonInfoResponseDto> likedLessons = memberService.getMyLikedLessons(userEmail);

        return ResponseEntity.ok(likedLessons);
    }

    @Operation(summary = "로그아웃", description = "로그아웃 시 리프레시 토큰을 삭제합니다.")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal User sessionMember) {
        String memberEmail = sessionMember.getUsername();
        memberService.logout(memberEmail);
        return ResponseEntity.ok().build();
    }
}
