package com.capstone.bowlingbling.domain.gathering.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GatheringDto {
    private Long id;
    private String name;
    private Integer minAverage;
    private Integer maxAverage;
    private String description;
    private String location;
    private LocalDateTime date;
    private Integer maxParticipants;
    private Integer currentParticipants;
}
