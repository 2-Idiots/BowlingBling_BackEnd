package com.capstone.bowlingbling.domain.comment.controller;

import com.capstone.bowlingbling.domain.comment.dto.request.CommentRequestDto;
import com.capstone.bowlingbling.domain.comment.dto.response.ClubBoardCommentResponseDto;
import com.capstone.bowlingbling.domain.comment.service.ClubBoardCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/clubs/{clubId}/board/{postId}/comments")
public class ClubBoardCommentController {

    private final ClubBoardCommentService commentService;

    // 6. 댓글 목록 조회
    @GetMapping
    public ResponseEntity<List<ClubBoardCommentResponseDto>> getComments(@PathVariable Long clubId, @PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getComments(clubId, postId));
    }

    // 7. 댓글 작성
    @PostMapping
    public ResponseEntity<String> createComment(@PathVariable Long clubId,
                                                @PathVariable Long postId,
                                                @RequestBody CommentRequestDto content,
                                                @AuthenticationPrincipal User user) {
        String memberEmail = user.getUsername();
        commentService.createComment(clubId, postId, memberEmail, content);
        return ResponseEntity.ok("댓글이 작성되었습니다.");
    }

    // 8. 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long clubId,
                                              @PathVariable Long postId,
                                              @PathVariable Long commentId,
                                              @AuthenticationPrincipal User user) {
        String memberEmail = user.getUsername();
        commentService.deleteComment(clubId, postId, commentId, memberEmail);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}