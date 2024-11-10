package com.capstone.bowlingbling.domain.club.dto.clubBoard;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthorDto {
    private final Long id;
    private final String name;
    private final String image;
}
