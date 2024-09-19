package com.capstone.bowlingbling.domain.club.controller;

import com.capstone.bowlingbling.domain.club.dto.request.ClubJoinRequestStatusDto;
import com.capstone.bowlingbling.domain.club.dto.request.ClubRequestDto;
import com.capstone.bowlingbling.domain.club.dto.response.ClubJoinResponseDto;
import com.capstone.bowlingbling.domain.club.dto.response.ClubResponseDto;
import com.capstone.bowlingbling.domain.club.dto.response.ClubResponseListDto;
import com.capstone.bowlingbling.domain.club.service.ClubService;
import com.capstone.bowlingbling.domain.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/club")
@RequiredArgsConstructor
@Tag(name = "Club", description = "Club API")
public class ClubController {

    private final ClubService clubService;

    @GetMapping
    @Operation(summary = "동호회 전체 리스트 조회", description = "모든 동호회의 이름, 회원 수, 회장 닉네임을 반환합니다.")
    public Page<ClubResponseListDto> getAllClubs() {
        return clubService.getAllClubs(Pageable.ofSize(10));
    }

    @GetMapping("/{clubId}")
    @Operation(summary = "동호회 단일 조회", description = "동호회의 상세 정보를 조회합니다. (동호회명, 회원 수, 회장 닉네임 등)")
    public ResponseEntity<ClubResponseDto> getClubById(@PathVariable Long clubId) {
        ClubResponseDto club = clubService.getClubById(clubId);
        return ResponseEntity.ok(club);
    }

    @Operation(summary = "동호회 생성", description = "새로운 동호회를 생성합니다.")
    @PostMapping("/create")
    public ResponseEntity<ClubResponseDto> createClub(@RequestBody ClubRequestDto clubDto, @AuthenticationPrincipal User sessionMember) {
        String memberEmail = sessionMember.getUsername();
        ClubResponseDto createdClub = clubService.createClub(clubDto, memberEmail);
        return ResponseEntity.ok(createdClub);
    }

    @PostMapping("/{clubId}/join")
    @Operation(summary = "동호회 가입 신청", description = "동호회에 가입 신청을 합니다.")
    public ResponseEntity<Void> joinClub(@PathVariable Long clubId, @AuthenticationPrincipal User sessionMember) {
        String memberEmail = sessionMember.getUsername();
        clubService.joinClub(clubId, memberEmail);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "동호회 가입 요청 목록 조회", description = "동호회장이 가입 요청한 회원 목록을 조회합니다.")
    @GetMapping("/{clubId}/joinList")
    public ResponseEntity<Page<ClubJoinResponseDto>> getJoinRequests(@PathVariable Long clubId, @AuthenticationPrincipal User sessionMember) {
        String memberEmail = sessionMember.getUsername();
        Page<ClubJoinResponseDto> joinResponse = clubService.getJoinRequests(clubId, memberEmail, Pageable.ofSize(10));
        return ResponseEntity.ok(joinResponse);
    }

    @Operation(summary = "동호회 가입 요청 처리", description = "동호회장이 가입 요청을 승인 또는 거부합니다.")
    @PostMapping("/{clubId}/{memberId}/accept")
    public ResponseEntity<Void> acceptJoinRequest(@PathVariable Long clubId, @PathVariable Long memberId, @RequestBody ClubJoinRequestStatusDto statusDto, @AuthenticationPrincipal User sessionMember) {
        String memberEmail = sessionMember.getUsername();
        clubService.acceptJoinRequest(clubId, memberId, statusDto, memberEmail);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{clubId}")
    @Operation(summary = "동호회 수정", description = "동호회 정보를 수정합니다. (동호회장 또는 관리자만 가능)")
    public ResponseEntity<ClubResponseDto> updateClub(@PathVariable Long clubId, @RequestBody ClubRequestDto clubDto, @AuthenticationPrincipal User sessionMember) {
        String memberEmail = sessionMember.getUsername();
        ClubResponseDto updatedClub = clubService.updateClub(clubId, clubDto, memberEmail);
        return ResponseEntity.ok(updatedClub);
    }

    @DeleteMapping("/{clubId}")
    @Operation(summary = "동호회 삭제", description = "동호회를 삭제합니다. (동호회장 또는 관리자만 가능)")
    public ResponseEntity<Void> deleteClub(@PathVariable Long clubId, @AuthenticationPrincipal User sessionMember) {
        String memberEmail = sessionMember.getUsername();
        clubService.deleteClub(clubId, memberEmail);
        return ResponseEntity.ok().build();
    }
}
