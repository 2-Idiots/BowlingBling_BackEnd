package com.capstone.bowlingbling.domain.comment.controller;

import com.capstone.bowlingbling.domain.comment.dto.request.CommentRequestDto;
import com.capstone.bowlingbling.domain.comment.dto.response.CommentResponseDto;
import com.capstone.bowlingbling.domain.comment.service.CommunityCommentService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/community/{communityId}/comments")
public class CommunityCommentController {

    @Autowired
    private CommunityCommentService communityCommentService;

    @GetMapping
    @Operation(summary = "게시판 댓글 조회", description = "해당 게시판에 속해있는 댓글들을 페이징하여 조회합니다.")
    public ResponseEntity<Page<CommentResponseDto>> getComments(@PathVariable Long communityId) {
        Page<CommentResponseDto> comments = communityCommentService.getComments(communityId, Pageable.ofSize(10));
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/save")
    @Operation(summary = "게시판 댓글 저장", description = "새로운 댓글을 저장합니다.")
    public ResponseEntity<CommentResponseDto> saveComment(@PathVariable Long communityId, @RequestBody CommentRequestDto requestDto, @AuthenticationPrincipal User user) {
        String memberEmail = user.getUsername();
        CommentResponseDto responseDto = communityCommentService.saveComment(communityId, requestDto, memberEmail);
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/update/{commentId}")
    @Operation(summary = "게시판 댓글 수정", description = "작성한 댓글을 수정합니다. (작성자만 가능)")
    public ResponseEntity<CommentResponseDto> updateComment(@PathVariable Long communityId, @PathVariable Long commentId, @RequestBody CommentRequestDto requestDto, @AuthenticationPrincipal User user) {
        String memberEmail = user.getUsername();
        CommentResponseDto responseDto = communityCommentService.updateComment(communityId, commentId, requestDto, memberEmail);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/delete/{commentId}")
    @Operation(summary = "게시판 댓글 삭제", description = "작성한 댓글을 삭제합니다.")
    public ResponseEntity<Void> deleteComment(@PathVariable Long communityId, @PathVariable Long commentId, @AuthenticationPrincipal User user) {
        String memberEmail = user.getUsername();
        communityCommentService.deleteComment(communityId, commentId, memberEmail);
        return ResponseEntity.noContent().build();
    }
}
