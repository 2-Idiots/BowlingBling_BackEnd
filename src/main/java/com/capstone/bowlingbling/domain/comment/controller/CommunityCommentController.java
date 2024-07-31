package com.capstone.bowlingbling.domain.comment.controller;

import com.capstone.bowlingbling.domain.comment.dto.request.CommunityCommentRequestDto;
import com.capstone.bowlingbling.domain.comment.dto.response.CommunityCommentResponseDto;
import com.capstone.bowlingbling.domain.comment.service.CommunityCommentService;
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
    public ResponseEntity<Page<CommunityCommentResponseDto>> getComments(@PathVariable Long communityId) {
        Page<CommunityCommentResponseDto> comments = communityCommentService.getComments(communityId, Pageable.ofSize(10));
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/save")
    public ResponseEntity<CommunityCommentResponseDto> saveComment(@PathVariable Long communityId, @RequestBody CommunityCommentRequestDto requestDto, @AuthenticationPrincipal User user) {
        String memberEmail = user.getUsername();
        CommunityCommentResponseDto responseDto = communityCommentService.saveComment(communityId, requestDto, memberEmail);
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/update/{commentId}")
    public ResponseEntity<CommunityCommentResponseDto> updateComment(@PathVariable Long communityId, @PathVariable Long commentId, @RequestBody CommunityCommentRequestDto requestDto, @AuthenticationPrincipal User user) {
        String memberEmail = user.getUsername();
        CommunityCommentResponseDto responseDto = communityCommentService.updateComment(communityId, commentId, requestDto, memberEmail);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long communityId, @PathVariable Long commentId, @AuthenticationPrincipal User user) {
        String memberEmail = user.getUsername();
        communityCommentService.deleteComment(communityId, commentId, memberEmail);
        return ResponseEntity.noContent().build();
    }
}
