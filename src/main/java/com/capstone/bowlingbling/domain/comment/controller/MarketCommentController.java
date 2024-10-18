package com.capstone.bowlingbling.domain.comment.controller;

import com.capstone.bowlingbling.domain.comment.dto.request.CommentRequestDto;
import com.capstone.bowlingbling.domain.comment.dto.response.CommentResponseDto;
import com.capstone.bowlingbling.domain.comment.service.MarketCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/market/{marketId}/comments")
@Tag(name = "Market comments", description = "마켓 댓글 API")
public class MarketCommentController {

    @Autowired
    private MarketCommentService marketCommentService;

    @Operation(summary = "댓글 조회", description = "특정 마켓에 달린 모든 댓글을 조회합니다. 한 번에 10개씩 조회됩니다.")
    @GetMapping
    public ResponseEntity<Page<CommentResponseDto>> getComments(@PathVariable Long marketId) {
        Page<CommentResponseDto> comments = marketCommentService.getComments(marketId, Pageable.ofSize(10));
        return ResponseEntity.ok(comments);
    }

    @Operation(summary = "댓글 작성", description = "특정 마켓에 댓글을 작성합니다.")
    @PostMapping("/save")
    public ResponseEntity<CommentResponseDto> saveComment(@PathVariable Long marketId, @RequestBody CommentRequestDto requestDto, @AuthenticationPrincipal User user) {
        String memberEmail = user.getUsername();
        CommentResponseDto responseDto = marketCommentService.saveComment(marketId, requestDto, memberEmail);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "댓글 수정", description = "특정 마켓 댓글을 수정합니다.")
    @PatchMapping("/update/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(@PathVariable Long marketId, @PathVariable Long commentId, @RequestBody CommentRequestDto requestDto, @AuthenticationPrincipal User user) {
        String memberEmail = user.getUsername();
        CommentResponseDto responseDto = marketCommentService.updateComment(marketId, commentId, requestDto, memberEmail);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "댓글 삭제", description = "특정 마켓 댓글을 삭제합니다.")
    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long marketId, @PathVariable Long commentId, @AuthenticationPrincipal User user) {
        String memberEmail = user.getUsername();
        marketCommentService.deleteComment(marketId, commentId, memberEmail);
        return ResponseEntity.noContent().build();
    }
}
