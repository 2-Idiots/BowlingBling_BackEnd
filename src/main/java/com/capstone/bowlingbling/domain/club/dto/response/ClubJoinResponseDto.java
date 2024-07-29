package com.capstone.bowlingbling.domain.club.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubJoinResponseDto {
    private Long id;
    private String imageUrl;
    private String sex;
    private Integer age;
    private String phonenum;
}
