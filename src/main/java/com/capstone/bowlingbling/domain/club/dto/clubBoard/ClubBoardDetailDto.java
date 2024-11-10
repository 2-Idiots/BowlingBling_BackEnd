package com.capstone.bowlingbling.domain.club.dto.clubBoard;

import com.capstone.bowlingbling.global.enums.ClubCategory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ClubBoardDetailDto {
    private final Long id;
    private final Long clubId;
    private final String title;
    private final String content;
    private final ClubCategory category;
    private final AuthorDto author;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final Integer viewCount;
    private final Integer commentCount;
    private final Boolean isPinned;
    private final List<AttachmentDto> attachments;
}