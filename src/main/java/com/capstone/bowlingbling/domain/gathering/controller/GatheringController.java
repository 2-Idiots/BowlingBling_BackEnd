package com.capstone.bowlingbling.domain.gathering.controller;

import com.capstone.bowlingbling.domain.gathering.dto.request.GatheringRequestDto;
import com.capstone.bowlingbling.domain.gathering.dto.request.GatheringUpdateRequestDto;
import com.capstone.bowlingbling.domain.gathering.dto.response.GatheringDetailResponseDto;
import com.capstone.bowlingbling.domain.gathering.service.GatheringService;
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
@RequestMapping("/gatherings")
@Tag(name = "gatherings", description = "번개 모임 API")
@RequiredArgsConstructor
public class GatheringController {

    private final GatheringService gatheringService;

    @GetMapping
    @Operation(summary = "전체 번개 모임 조회", description = "모든 번개 모임을 페이징 처리하여 조회합니다.")
    public ResponseEntity<Page<GatheringDetailResponseDto>> getAllGatherings(@RequestParam(defaultValue = "0") int page) {
        Page<GatheringDetailResponseDto> gatherings = gatheringService.getAllGatherings(PageRequest.of(page, 10));
        return ResponseEntity.ok(gatherings);
    }

    @GetMapping("/{gatheringId}")
    @Operation(summary = "단일 번개 모임 조회", description = "번개 모임의 상세 정보를 조회합니다.")
    public ResponseEntity<GatheringDetailResponseDto> getGatheringById(@PathVariable Long gatheringId) {
        GatheringDetailResponseDto gathering = gatheringService.getGathering(gatheringId);
        return ResponseEntity.ok(gathering);
    }

    @PostMapping(value ="/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "번개 모임 생성", description = "새로운 번개 모임을 생성합니다.")
    public ResponseEntity<String> createGathering(@RequestPart(value = "request") GatheringRequestDto gatheringRequestDto,
                                                  @AuthenticationPrincipal User sessionMember,
                                                  @Parameter(description = "업로드할 파일 목록", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
                                                  @RequestPart(value = "files", required = false) List<MultipartFile> files) throws IOException {
        String memberEmail = sessionMember.getUsername();
        gatheringService.createGathering(gatheringRequestDto, memberEmail, files);
        return ResponseEntity.ok("번개 모임 생성 완료");
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
    public ResponseEntity<Page<GatheringDetailResponseDto>> getMyGatherings(@AuthenticationPrincipal User sessionMember, @RequestParam(defaultValue = "0") int page) {
        String memberEmail = sessionMember.getUsername();
        Page<GatheringDetailResponseDto> gatherings = gatheringService.getMemberGatherings(memberEmail, PageRequest.of(page, 10));
        return ResponseEntity.ok(gatherings);
    }

    @PatchMapping(value = "/{gatheringId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "번개 모임 수정", description = "번개 모임 정보를 수정합니다.")
    public ResponseEntity<String> updateGathering(@PathVariable Long gatheringId,
                                                                      @RequestBody GatheringUpdateRequestDto gatheringRequestDto,
                                                                      @AuthenticationPrincipal User sessionMember,
                                                                      @Parameter(description = "업로드할 파일 목록", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
                                                                          @RequestPart(value = "files", required = false) List<MultipartFile> files) throws IOException {
        String memberEmail = sessionMember.getUsername();
        gatheringService.updateGathering(gatheringId, gatheringRequestDto, memberEmail, files);
        return ResponseEntity.ok("번개 모임이 성공적으로 수정되었습니다.");
    }

    @DeleteMapping("/{gatheringId}")
    @Operation(summary = "번개 모임 삭제", description = "번개 모임을 삭제합니다.")
    public ResponseEntity<Void> deleteGathering(@PathVariable Long gatheringId, @AuthenticationPrincipal User sessionMember) {
        String memberEmail = sessionMember.getUsername();
        gatheringService.deleteGathering(gatheringId, memberEmail);
        return ResponseEntity.ok().build();
    }
}
