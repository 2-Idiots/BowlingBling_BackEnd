package com.capstone.bowlingbling.domain.club.controller;

import com.capstone.bowlingbling.domain.club.dto.request.ClubCreateDto;
import com.capstone.bowlingbling.domain.club.dto.request.ClubJoinRequestDto;
import com.capstone.bowlingbling.domain.club.dto.request.ClubMembersRoleUpdateDto;
import com.capstone.bowlingbling.domain.club.dto.response.ClubDetailResponseDto;
import com.capstone.bowlingbling.domain.club.dto.response.ClubListResponseDto;
import com.capstone.bowlingbling.domain.club.dto.response.ClubMemberListResponseDto;
import com.capstone.bowlingbling.domain.club.service.ClubService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/club")
@RequiredArgsConstructor
@Tag(name = "Club", description = "동호회 API")
public class ClubController {

    private final ClubService clubService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)  // 멀티파트 데이터 타입 지원
    @Operation(
            summary = "동호회 생성",
            description = "새로운 동호회를 생성합니다."
    )
    public ResponseEntity<String> createLesson(
            @Parameter(hidden = true) @AuthenticationPrincipal User sessionMember,
            @RequestPart(value = "request") ClubCreateDto request,
            @Parameter(description = "업로드할 파일 목록", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestPart(value = "files", required = false) List<MultipartFile> files) throws IOException {  // 파일 업로드 파트 추가

        String leaderEmail = sessionMember.getUsername();
        clubService.createClub(request, leaderEmail, files);

        return ResponseEntity.ok("클럽이 성공적으로 생성되었습니다.");
    }

    @GetMapping
    @Operation(summary = "동호회 목록 조회", description = "페이지네이션 된 동호회 목록을 조회합니다.")
    public ResponseEntity<Page<ClubListResponseDto>> getClubs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int limit) {
        Page<ClubListResponseDto> clubs = clubService.getClubs(PageRequest.of(page, limit));
        return ResponseEntity.ok(clubs);
    }

    @GetMapping("/{clubId}")
    @Operation(summary = "동호회 상세 조회", description = "특정 동호회의 세부 정보를 조회합니다.")
    public ResponseEntity<ClubDetailResponseDto> getClubDetail(@PathVariable Long clubId) {
        ClubDetailResponseDto clubDetail = clubService.getClubDetail(clubId);
        return ResponseEntity.ok(clubDetail);
    }

    @GetMapping("/{clubId}/members")
    @Operation(summary = "동호회 회원 목록 조회", description = "동호회의 회원 목록을 조회합니다.")
    public ResponseEntity<List<ClubMemberListResponseDto>> getClubMembers(@PathVariable Long clubId) {
        List<ClubMemberListResponseDto> members = clubService.getClubMembers(clubId);
        return ResponseEntity.ok(members);
    }

    @PatchMapping("/{clubId}/members/{userId}/role")
    @Operation(summary = "회원 역할 변경", description = "동호회 회원의 역할을 변경합니다.")
    public ResponseEntity<String> changeMemberRole(
            @AuthenticationPrincipal User sessionMember,
            @PathVariable Long clubId,
            @PathVariable Long userId,
            @RequestBody ClubMembersRoleUpdateDto request) {

        String leaderEmail = sessionMember.getUsername();
        clubService.updateMemberRole(clubId, userId, request, leaderEmail);
        return ResponseEntity.ok("회원 역할이 성공적으로 변경되었습니다.");
    }

    @PostMapping("/{id}/join")
    @Operation(summary = "클럽 가입 요청", description = "클럽에 가입 요청을 합니다.")
    public ResponseEntity<String> requestToJoinClub(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal User sessionMember) {

        String memberEmail = sessionMember.getUsername();
        clubService.requestToJoinClub(id, memberEmail);
        return ResponseEntity.ok("클럽 가입 요청이 성공적으로 전송되었습니다.");
    }

    @PostMapping("/{id}/join-request")
    @Operation(summary = "동호회 가입 요청 결정", description = "동호회장이 가입 요청을 수락 또는 거절합니다.")
    public ResponseEntity<String> decideJoinRequest(
            @PathVariable Long id,
            @RequestBody ClubJoinRequestDto requestDto,
            @Parameter(hidden = true) @AuthenticationPrincipal User sessionLeader) {

        String leaderEmail = sessionLeader.getUsername();
        String state = clubService.decideJoinRequest(id, requestDto, leaderEmail);

        return ResponseEntity.ok("가입 요청이 " + state + " 되었습니다.");
    }
}
