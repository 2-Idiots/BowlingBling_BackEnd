package com.capstone.bowlingbling.domain.club.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class ClubJoinRequestDto {
    private int averageScore;
    private String experience;
    private String motivation;
    private List<Boolean> availability;
}