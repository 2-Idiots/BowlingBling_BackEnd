package com.capstone.bowlingbling.domain.center.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CenterSaveRequestDto {

    private String location;
    private String businessName;
    private String operatingHours;
    private String announcements;
    private Integer laneCount;
}