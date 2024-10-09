package com.capstone.bowlingbling.domain.gathering.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class GatheringDetailResponseDto {
    private String leadername;
    private Long id;
    private String title;
    private Integer minAverage;
    private Integer maxAverage;
    private String description;
    private String location;
    private LocalDateTime date;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private List<String> images;
    private String lat;
    private String lng;
//    private PlaceDto place;
}