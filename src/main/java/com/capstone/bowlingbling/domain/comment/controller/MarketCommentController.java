package com.capstone.bowlingbling.domain.comment.controller;

import com.capstone.bowlingbling.domain.comment.dto.request.MarketCommentRequestDto;
import com.capstone.bowlingbling.domain.comment.dto.response.MarketCommentResponseDto;
import com.capstone.bowlingbling.domain.comment.service.MarketCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Market/{MarketId}/comments")
public class MarketCommentController {

    @Autowired
    private MarketCommentService marketCommentService;

    @GetMapping
    public ResponseEntity<Page<MarketCommentResponseDto>> getComments(@PathVariable Long marketId) {
        Page<MarketCommentResponseDto> comments = marketCommentService.getComments(marketId, Pageable.ofSize(10));
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/save")
    public ResponseEntity<MarketCommentResponseDto> saveComment(@PathVariable Long marketId, @RequestBody MarketCommentRequestDto requestDto, @AuthenticationPrincipal User user) {
        String memberEmail = user.getUsername();
        MarketCommentResponseDto responseDto = marketCommentService.saveComment(marketId, requestDto, memberEmail);
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/update/{commentId}")
    public ResponseEntity<MarketCommentResponseDto> updateComment(@PathVariable Long marketId, @PathVariable Long commentId, @RequestBody MarketCommentRequestDto requestDto, @AuthenticationPrincipal User user) {
        String memberEmail = user.getUsername();
        MarketCommentResponseDto responseDto = marketCommentService.updateComment(marketId, commentId, requestDto, memberEmail);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long marketId, @PathVariable Long commentId, @AuthenticationPrincipal User user) {
        String memberEmail = user.getUsername();
        marketCommentService.deleteComment(marketId, commentId, memberEmail);
        return ResponseEntity.noContent().build();
    }
}
