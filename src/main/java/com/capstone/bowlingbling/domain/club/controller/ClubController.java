package com.capstone.bowlingbling.domain.club.controller;

import com.capstone.bowlingbling.domain.club.dto.request.*;
import com.capstone.bowlingbling.domain.club.dto.response.*;
import com.capstone.bowlingbling.domain.club.service.ClubJoinListService;
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
@RequestMapping("/clubs")
@RequiredArgsConstructor
@Tag(name = "Club", description = "동호회 API")
public class ClubController {

    private final ClubService clubService;
    private final ClubJoinListService clubJoinListService;

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

    @PatchMapping("/{clubId}/settings")
    @Operation(summary = "동호회 설정 수정", description = "동호회 설정을 수정합니다.")
    public ResponseEntity<?> updateClubSettings(
            @PathVariable Long clubId,
            @AuthenticationPrincipal User sessionMember,
            @RequestPart("data") ClubUpdateDto updateDto,
            @Parameter(description = "업로드할 파일 목록", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws IOException {

        String memberEmail = sessionMember.getUsername();
        clubService.updateClubSettings(clubId, memberEmail, updateDto, images);
        return ResponseEntity.ok("동호회 설정이 성공적으로 수정되었습니다.");
    }

    @PatchMapping("/{clubId}/recruitment")
    @Operation(summary = "모집 상태 변경", description = "동호회의 모집 상태를 변경합니다.")
    public ResponseEntity<?> updateRecruitmentStatus(
            @PathVariable Long clubId,
            @AuthenticationPrincipal User sessionMember,
            @RequestBody ClubRecruitmentUpdateDto recruitmentDto) {

        String memberEmail = sessionMember.getUsername();
        clubService.updateRecruitmentStatus(clubId, memberEmail, recruitmentDto);
        return ResponseEntity.ok("모집 상태가 성공적으로 변경되었습니다.");
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

    @GetMapping("/{clubId}/members/{userId}")
    @Operation(summary = "동호회 회원 디테일 조회", description = "동호회의 회원 디테일을 조회합니다.")
    public ResponseEntity<ClubMemberDetailResponseDto> getClubMemberDetail(@AuthenticationPrincipal User sessionMember,
                                                                      @PathVariable Long clubId,
                                                                      @PathVariable Long userId) {
        String leaderEmail = sessionMember.getUsername();
        ClubMemberDetailResponseDto member = clubService.getClubMemberDetail(clubId, userId ,leaderEmail);
        return ResponseEntity.ok(member);
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

    @PatchMapping("/{clubId}/members/{userId}/status")
    @Operation(summary = "회원 상태 변경", description = "동호회 회원의 상태를 변경합니다.")
    public ResponseEntity<String> changeMemberStatus(
            @AuthenticationPrincipal User sessionMember,
            @PathVariable Long clubId,
            @PathVariable Long userId,
            @RequestBody ClubMemberStatusUpdateDto request) {

        String leaderEmail = sessionMember.getUsername();
        clubService.updateMemberStatus(clubId, userId, request, leaderEmail);
        return ResponseEntity.ok("회원 상태가 성공적으로 변경되었습니다.");
    }

    @PostMapping("/{clubId}/join")
    @Operation(summary = "가입 신청", description = "클럽 가입을 신청합니다.")
    public ResponseEntity<String> createJoinRequest(@PathVariable Long clubId, @RequestBody ClubJoinRequestDto request, @AuthenticationPrincipal User sessionMember) {
        String memberEmail = sessionMember.getUsername();
        Long requestId = clubJoinListService.createJoinRequest(clubId, memberEmail, request);
        return ResponseEntity.ok("가입 요청이 성공적으로 되었습니다. 요청 ID : " + requestId);
    }

    @GetMapping("/{clubId}/join-requests")
    @Operation(summary = "가입 신청 목록 조회", description = "클럽의 가입 신청 목록을 조회합니다.")
    public ResponseEntity<Page<ClubJoinListResponseDto>> getJoinRequests(@PathVariable Long clubId, @RequestParam(defaultValue = "0") int page, @AuthenticationPrincipal User sessionMember) {
        String leaderEmail = sessionMember.getUsername();
        Page<ClubJoinListResponseDto> requests = clubJoinListService.getJoinRequests(clubId, PageRequest.of(page, 10), leaderEmail);
        return ResponseEntity.ok(requests);
    }

    @PostMapping("/{clubId}/join-requests/{requestId}/approve")
    @Operation(summary = "가입 신청 승인", description = "클럽 가입 신청을 승인합니다.")
    public ResponseEntity<String> approveJoinRequest(@PathVariable Long clubId, @PathVariable Long requestId, @AuthenticationPrincipal User sessionMember) {
        String leaderEmail = sessionMember.getUsername();
        clubJoinListService.approveJoinRequest(clubId, requestId, leaderEmail);
        return ResponseEntity.ok("요청 " + requestId + "ID 에 대한 가입 승인이 정상적으로 처리되었습니다.");
    }

    @PostMapping("/{clubId}/join-requests/{requestId}/reject")
    @Operation(summary = "가입 신청 거절", description = "클럽 가입 신청을 거절합니다.")
    public ResponseEntity<String> rejectJoinRequest(@PathVariable Long clubId, @PathVariable Long requestId, @AuthenticationPrincipal User sessionMember) {
        String leaderEmail = sessionMember.getUsername();
        clubJoinListService.rejectJoinRequest(clubId, requestId, leaderEmail);
        return ResponseEntity.ok("요청 " + requestId + "ID 에 대한 가입 거절이 정상적으로 처리되었습니다.");
    }

    @DeleteMapping("/{clubId}/members/{userId}")
    @Operation(summary = "회원 강제 탈퇴", description = "동호회 회원을 강제로 탈퇴시킵니다.")
    public ResponseEntity<String> removeClubMember(
            @AuthenticationPrincipal User sessionMember,
            @PathVariable Long clubId,
            @PathVariable Long userId,
            @RequestBody ClubMemberRemoveDto request) {

        String leaderEmail = sessionMember.getUsername();
        clubService.removeMember(clubId, userId, request.getReason(), leaderEmail);

        return ResponseEntity.ok("회원이 강제 탈퇴되었습니다.");
    }
}
