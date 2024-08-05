package com.capstone.bowlingbling.domain.club.dto.request;

import com.capstone.bowlingbling.domain.place.dto.PlaceDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class ClubRequestDto {
    private String clubname;
    private String introduction;
    private PlaceDto place;
}