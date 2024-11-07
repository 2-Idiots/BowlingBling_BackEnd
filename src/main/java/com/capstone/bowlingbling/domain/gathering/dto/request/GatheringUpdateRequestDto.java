package com.capstone.bowlingbling.domain.gathering.dto.request;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GatheringUpdateRequestDto {
    private String title;
    private Integer minAverage;
    private Integer maxAverage;
    private String description;
    private String location;
    private LocalDateTime date;
    private Integer maxParticipants;
    private String lat;
    private String lng;
}
