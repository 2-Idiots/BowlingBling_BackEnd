package com.capstone.bowlingbling.domain.comment.controller;

import com.capstone.bowlingbling.domain.comment.dto.request.CommentRequestDto;
import com.capstone.bowlingbling.domain.comment.dto.response.CommentResponseDto;
import com.capstone.bowlingbling.domain.comment.service.LessonCommentService;
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
@RequestMapping("/lesson/{lessonId}/comments")
@Tag(name = "Lesson comments", description = "레슨 댓글 API")
public class LessonCommentController {

    @Autowired
    private LessonCommentService lessonCommentService;

    @Operation(summary = "댓글 조회", description = "특정 레슨에 달린 모든 댓글을 조회합니다. 한 번에 10개씩 조회됩니다.")
    @GetMapping
    public ResponseEntity<Page<CommentResponseDto>> getComments(@PathVariable Long lessonId) {
        Page<CommentResponseDto> comments = lessonCommentService.getComments(lessonId, Pageable.ofSize(10));
        return ResponseEntity.ok(comments);
    }

    @Operation(summary = "댓글 작성", description = "특정 레슨에 댓글을 작성합니다.")
    @PostMapping("/save")
    public ResponseEntity<String> saveComment(@PathVariable Long lessonId, @RequestBody CommentRequestDto requestDto, @AuthenticationPrincipal User user) {
        String memberEmail = user.getUsername();
        lessonCommentService.saveComment(lessonId, requestDto, memberEmail);
        return ResponseEntity.ok("댓글 저장 성공");
    }

    @Operation(summary = "댓글 수정", description = "특정 레슨 댓글을 수정합니다.")
    @PatchMapping("/{commentId}/update")
    public ResponseEntity<String> updateComment(@PathVariable Long lessonId, @PathVariable Long commentId, @RequestBody CommentRequestDto requestDto, @AuthenticationPrincipal User user) {
        String memberEmail = user.getUsername();
        lessonCommentService.updateComment(lessonId, commentId, requestDto, memberEmail);
        return ResponseEntity.ok("레슨 수정 성공");
    }

    @Operation(summary = "댓글 삭제", description = "특정 마켓 댓글을 삭제합니다.")
    @DeleteMapping("/{commentId}/delete")
    public ResponseEntity<String> deleteComment(@PathVariable Long lessonId, @PathVariable Long commentId, @AuthenticationPrincipal User user) {
        String memberEmail = user.getUsername();
        lessonCommentService.deleteComment(lessonId, commentId, memberEmail);
        return ResponseEntity.ok("레슨 삭제 성공");
    }
}
