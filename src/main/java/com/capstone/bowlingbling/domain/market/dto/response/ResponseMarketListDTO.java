package com.capstone.bowlingbling.domain.market.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@ToString
@Getter
public class ResponseMarketListDTO {
    //TODO place 추가
    private Long id;
    private String title;
    private int sales;
}
