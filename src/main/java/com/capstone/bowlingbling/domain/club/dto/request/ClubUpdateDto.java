package com.capstone.bowlingbling.domain.club.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ClubUpdateDto {
    private String name;
    private String description;
    private String location;
    private int maxMembers;
    private String category;
    private String requirements;
    private int monthlyFee;
    private List<String> meetingDays;
    private int averageScore;
}