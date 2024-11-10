package com.capstone.bowlingbling.domain.comment.dto.response;

import com.capstone.bowlingbling.domain.club.dto.clubBoard.AuthorDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ClubBoardCommentResponseDto {
    private Long id;
    private Long postId;
    private String content;
    private AuthorDto author; // 댓글 작성자 정보
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
