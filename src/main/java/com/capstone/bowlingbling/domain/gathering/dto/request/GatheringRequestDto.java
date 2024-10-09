package com.capstone.bowlingbling.domain.gathering.dto.request;

import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.domain.place.dto.PlaceDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class GatheringRequestDto {
    private String title;
    private Integer minAverage;
    private Integer maxAverage;
    private String description;
    private String location;
    private LocalDateTime date;
    private Integer maxParticipants;
    private String lat;
    private String lng;
//    private PlaceDto place;
}
