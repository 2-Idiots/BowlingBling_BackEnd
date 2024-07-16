package com.capstone.bowlingbling.domain.community.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class CommunitySaveRequestDto {

    private String category;

    private String title;

    private String contents;
}
