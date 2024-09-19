package com.capstone.bowlingbling.domain.gathering.controller;

import com.capstone.bowlingbling.domain.gathering.dto.request.GatheringRequestDto;
import com.capstone.bowlingbling.domain.gathering.dto.response.GatheringCreateResponseDto;
import com.capstone.bowlingbling.domain.gathering.service.GatheringService;
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
@RequestMapping("/api/gatherings")
@Tag(name = "gatherings", description = "번개 모임 API")
@RequiredArgsConstructor
public class GatheringController {

    private final GatheringService gatheringService;

    @GetMapping
    @Operation(summary = "전체 번개 모임 조회", description = "모든 번개 모임을 페이징 처리하여 조회합니다.")
    public ResponseEntity<Page<GatheringRequestDto>> getAllGatherings() {
        Page<GatheringRequestDto> gatherings = gatheringService.getAllGatherings(Pageable.ofSize(10));
        return ResponseEntity.ok(gatherings);
    }

    @GetMapping("/{gatheringId}")
    @Operation(summary = "단일 번개 모임 조회", description = "번개 모임의 상세 정보를 조회합니다.")
    public ResponseEntity<GatheringRequestDto> getGatheringById(@PathVariable Long gatheringId) {
        GatheringRequestDto gathering = gatheringService.getGathering(gatheringId);
        return ResponseEntity.ok(gathering);
    }

    @PostMapping("/create")
    @Operation(summary = "번개 모임 생성", description = "새로운 번개 모임을 생성합니다.")
    public ResponseEntity<GatheringCreateResponseDto> createGathering(@RequestBody GatheringRequestDto gatheringRequestDto, @AuthenticationPrincipal User sessionMember) {
        String memberEmail = sessionMember.getUsername();
        GatheringCreateResponseDto createdGathering = gatheringService.createGathering(gatheringRequestDto, memberEmail);
        return ResponseEntity.ok(createdGathering);
    }

    @PostMapping("/{gatheringId}/join")
    @Operation(summary = "번개 모임 가입", description = "번개 모임에 가입 신청을 합니다.")
    public ResponseEntity<String> joinGathering(@PathVariable Long gatheringId, @AuthenticationPrincipal User sessionMember) {
        String memberEmail = sessionMember.getUsername();
        gatheringService.joinGathering(gatheringId, memberEmail);
        return ResponseEntity.ok("가입 신청 완료");
    }

    @GetMapping("/my")
    @Operation(summary = "내 번개 모임 조회", description = "내가 생성하거나 가입한 번개 모임 목록을 조회합니다.")
    public ResponseEntity<Page<GatheringRequestDto>> getMyGatherings(@AuthenticationPrincipal User sessionMember) {
        String memberEmail = sessionMember.getUsername();
        Page<GatheringRequestDto> gatherings = gatheringService.getMemberGatherings(memberEmail, Pageable.ofSize(10));
        return ResponseEntity.ok(gatherings);
    }

    @PatchMapping("/{gatheringId}")
    @Operation(summary = "번개 모임 수정", description = "번개 모임 정보를 수정합니다.")
    public ResponseEntity<GatheringRequestDto> updateGathering(@PathVariable Long gatheringId, @RequestBody GatheringRequestDto gatheringRequestDto, @AuthenticationPrincipal User sessionMember) {
        String memberEmail = sessionMember.getUsername();
        GatheringRequestDto updatedGathering = gatheringService.updateGathering(gatheringId, gatheringRequestDto, memberEmail);
        return ResponseEntity.ok(updatedGathering);
    }

    @DeleteMapping("/{gatheringId}")
    @Operation(summary = "번개 모임 삭제", description = "번개 모임을 삭제합니다.")
    public ResponseEntity<Void> deleteGathering(@PathVariable Long gatheringId, @AuthenticationPrincipal User sessionMember) {
        String memberEmail = sessionMember.getUsername();
        gatheringService.deleteGathering(gatheringId, memberEmail);
        return ResponseEntity.ok().build();
    }
}
