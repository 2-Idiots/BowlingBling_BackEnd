package com.capstone.bowlingbling.domain.gathering.dto.request;

import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.domain.place.dto.PlaceDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GatheringRequestDto {
    private Member member;
    private Long id;
    private String name;
    private Integer minAverage;
    private Integer maxAverage;
    private String description;
    private String location;
    private LocalDateTime date;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private PlaceDto place;
}
