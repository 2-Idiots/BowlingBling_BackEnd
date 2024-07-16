package com.capstone.bowlingbling.domain.community.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class CommunityDetailResponseDto {
    private Long id;

    private String nickname;

    private String category;

    private String title;

    //TODO 댓글 추가하세요
}
