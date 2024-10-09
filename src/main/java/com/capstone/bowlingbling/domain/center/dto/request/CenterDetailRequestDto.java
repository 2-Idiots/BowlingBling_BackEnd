package com.capstone.bowlingbling.domain.center.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CenterDetailRequestDto {
    private String businessName;
    private String location;
    private String operatingHours;
    private String announcements;
    private int laneCount;
    private List<String> images;
    private String ownerName;
    private String lat;
    private String lng;
}