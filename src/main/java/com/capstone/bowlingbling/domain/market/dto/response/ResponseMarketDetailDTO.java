package com.capstone.bowlingbling.domain.market.dto.response;

import com.capstone.bowlingbling.domain.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ResponseMarketDetailDTO {
    private Long id;
    private Member member;
    private String title;
    private String contents;
    private int sales;
}