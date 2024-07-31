package com.capstone.bowlingbling.domain.comment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommunityCommentResponseDto {
    private Long id;
    private String comments;
    private String memberName;
    private String communityTitle;
}