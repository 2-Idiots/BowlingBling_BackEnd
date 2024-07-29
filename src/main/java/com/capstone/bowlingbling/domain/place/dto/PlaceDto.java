package com.capstone.bowlingbling.domain.place.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class PlaceDto {
    private String id;
    private String addressName;
    private String roadAddressName;
    private String buildingName;
    private String zoneNo;
    private double x; // 경도
    private double y; // 위도
    private String placeName;
}