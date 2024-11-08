package com.capstone.bowlingbling.domain.comment.controller;

import com.capstone.bowlingbling.domain.comment.dto.request.CommentRequestDto;
import com.capstone.bowlingbling.domain.comment.dto.response.CommentResponseDto;
import com.capstone.bowlingbling.domain.comment.service.CenterCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/centers/{centersId}/comments")
@Tag(name = "Center comments", description = "볼링장 댓글 API")
public class CenterCommentController {

    @Autowired
    private CenterCommentService centerCommentService;

    @Operation(summary = "댓글 조회", description = "특정 볼링장에 달린 모든 댓글을 조회합니다. 한 번에 10개씩 조회됩니다.")
    @GetMapping
    public ResponseEntity<Page<CommentResponseDto>> getComments(@PathVariable Long centersId, @RequestParam(defaultValue = "0") int page) {
        Page<CommentResponseDto> comments = centerCommentService.getAllComments(centersId, PageRequest.of(page, 10));
        return ResponseEntity.ok(comments);
    }

    @Operation(summary = "댓글 작성", description = "특정 볼링장에 댓글을 작성합니다.")
    @PostMapping("/save")
    public ResponseEntity<String> saveComment(@PathVariable Long centersId, @RequestBody CommentRequestDto requestDto, @AuthenticationPrincipal User user) {
        centerCommentService.saveComment(centersId, requestDto, user.getUsername());
        return ResponseEntity.ok("댓글 저장 성공");
    }

    @Operation(summary = "댓글 수정", description = "특정 볼링장 댓글을 수정합니다.")
    @PatchMapping("/{commentId}/update")
    public ResponseEntity<String> updateComment(@PathVariable Long centersId, @PathVariable Long commentId, @RequestBody CommentRequestDto requestDto, @AuthenticationPrincipal User user) {
        centerCommentService.updateComment(centersId, commentId, requestDto, user.getUsername());
        return ResponseEntity.ok("댓글 수정 성공");
    }

    @Operation(summary = "댓글 삭제", description = "특정 볼링장 댓글을 삭제합니다.")
    @DeleteMapping("/{commentId}/delete")
    public ResponseEntity<String> deleteComment(@PathVariable Long centersId, @PathVariable Long commentId, @AuthenticationPrincipal User user) {
        centerCommentService.deleteComment(centersId, commentId, user.getUsername());
        return ResponseEntity.ok("댓글 삭제 성공");
    }
}
