package com.capstone.bowlingbling.domain.comment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {
    private Long id;
    private String comments;
    private String memberName;
    private String image;
    private LocalDateTime modifiedAt;
    private boolean isDeleted;
}