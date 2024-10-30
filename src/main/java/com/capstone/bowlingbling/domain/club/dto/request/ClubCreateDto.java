package com.capstone.bowlingbling.domain.club.dto.request;

import com.capstone.bowlingbling.domain.club.domain.ClubJoinList;
import com.capstone.bowlingbling.global.enums.ClubTags;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubCreateDto {
    private String name;  //동호회 명
    private String description;
    private String location;
    private int maxMembers; //최대 인원
    private int averageScore; //최소 에버리지
    private String category;
    private String requirements;
    private Integer monthlyFee; //동호회비
    private List<Boolean> meetingDays;
}
