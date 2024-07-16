package com.capstone.bowlingbling.domain.market.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class RequestMarketSaveDTO {
    //TODO place, image 추가
    private String title;
    private String contents;
    private int sales;
}
