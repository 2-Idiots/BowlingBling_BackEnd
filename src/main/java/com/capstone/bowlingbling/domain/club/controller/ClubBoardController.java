package com.capstone.bowlingbling.domain.club.controller;

import com.capstone.bowlingbling.domain.club.dto.clubBoard.ClubBoardCreateDto;
import com.capstone.bowlingbling.domain.club.dto.clubBoard.ClubBoardDetailDto;
import com.capstone.bowlingbling.domain.club.dto.clubBoard.ClubBoardListResponseDto;
import com.capstone.bowlingbling.domain.club.service.ClubBoardService;
import com.capstone.bowlingbling.global.enums.ClubCategory;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/clubs/{clubId}/board")
public class ClubBoardController {

    private final ClubBoardService clubBoardService;

    // 1. 게시글 목록 조회
    @GetMapping
    public ResponseEntity<ClubBoardListResponseDto> getPosts(
            @PathVariable Long clubId,
            @RequestParam(required = false) ClubCategory category,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        ClubBoardListResponseDto response = clubBoardService.getPostList(clubId, category, searchType, keyword, page, size);
        return ResponseEntity.ok(response);
    }

    // 2. 게시글 상세 조회
    @GetMapping("/{postId}")
    public ResponseEntity<ClubBoardDetailDto> getPost(
            @PathVariable Long clubId,
            @PathVariable Long postId
    ) {
        ClubBoardDetailDto post = clubBoardService.getPostDetail(clubId, postId);
        return ResponseEntity.ok(post);
    }

    // 3. 게시글 작성
    @PostMapping
    public ResponseEntity<String> createPost(
            @PathVariable Long clubId,
            @Parameter(hidden = true) @AuthenticationPrincipal User sessionMember,
            @RequestPart(value = "request") @Valid ClubBoardCreateDto request,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
    ) throws IOException {
        String authorEmail = sessionMember.getUsername();
        clubBoardService.createPost(clubId, request, authorEmail, attachments);
        return ResponseEntity.status(201).body("게시글이 성공적으로 생성되었습니다.");
    }

    // 4. 게시글 수정
    @PatchMapping("/{postId}")
    public ResponseEntity<String> updatePost(
            @PathVariable Long clubId,
            @PathVariable Long postId,
            @Parameter(hidden = true) @AuthenticationPrincipal User sessionMember,
            @RequestPart(value = "request") ClubBoardCreateDto request,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments,
            @RequestParam(value = "keepAttachments", required = false) List<Long> keepAttachments
    ) throws IOException {
        String authorEmail = sessionMember.getUsername();
        clubBoardService.updatePost(clubId, postId, authorEmail, request, keepAttachments, attachments);
        return ResponseEntity.ok("게시글이 성공적으로 수정되었습니다.");
    }

    // 5. 게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long clubId,
            @PathVariable Long postId,
            @Parameter(hidden = true) @AuthenticationPrincipal User sessionMember
    ) {
        String authorEmail = sessionMember.getUsername();
        clubBoardService.deletePost(clubId, postId, authorEmail);
        return ResponseEntity.noContent().build();
    }

    // 9. 첨부파일 다운로드
    @GetMapping("/{postId}/attachments/{attachmentId}")
    public ResponseEntity<byte[]> downloadAttachment(
            @PathVariable Long clubId,
            @PathVariable Long postId,
            @PathVariable Long attachmentId
    ) throws IOException {
        return clubBoardService.downloadAttachment(clubId, postId, attachmentId);
    }

    // 10. 공지사항 목록 조회 (핀 고정만)
    @GetMapping("/pinned")
    public ResponseEntity<List<ClubBoardDetailDto>> getPinnedPosts(
            @PathVariable Long clubId
    ) {
        List<ClubBoardDetailDto> pinnedPosts = clubBoardService.getPinnedPosts(clubId);
        return ResponseEntity.ok(pinnedPosts);
    }
}