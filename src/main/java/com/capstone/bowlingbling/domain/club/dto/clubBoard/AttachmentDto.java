package com.capstone.bowlingbling.domain.club.dto.clubBoard;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AttachmentDto {
    private final Long id;
    private final String filename;
    private final String fileUrl;
    private final Long fileSize;
    private final String mimeType;
}
