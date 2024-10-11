package com.capstone.bowlingbling.domain.center.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CenterListResponseDto {
    private Long id;
    private String businessName;
    private String location;
    private String operatingHours;
    private Integer laneCount;
    private List<String> images;
    private String lat;
    private String lng;
}