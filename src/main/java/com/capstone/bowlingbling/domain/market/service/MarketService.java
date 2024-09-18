package com.capstone.bowlingbling.domain.market.service;

import com.capstone.bowlingbling.domain.market.domain.Market;
import com.capstone.bowlingbling.domain.market.dto.request.RequestMarketSaveDTO;
import com.capstone.bowlingbling.domain.market.dto.response.ResponseMarketDetailDTO;
import com.capstone.bowlingbling.domain.market.dto.response.ResponseMarketListDTO;
import com.capstone.bowlingbling.domain.market.repository.MarketRepository;
import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.domain.member.repository.MemberRepository;
import com.capstone.bowlingbling.global.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MarketService {

    private final MarketRepository marketRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public MarketService(MarketRepository marketRepository, MemberRepository memberRepository) {
        this.marketRepository = marketRepository;
        this.memberRepository = memberRepository;
    }

    public void saveMarket(RequestMarketSaveDTO marketDTO, String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자가 없습니다."));

        Market market = Market.builder()
                .member(member)
                .title(marketDTO.getTitle())
                .contents(marketDTO.getContents())
                .sales(marketDTO.getSales())
                .build();

        marketRepository.save(market);
    }

    public Page<ResponseMarketListDTO> getAllMarkets(Pageable pageable) {
        Page<Market> MarketPage = marketRepository.findAll(pageable);

        return MarketPage.map(market -> ResponseMarketListDTO.builder()
                .id(market.getId())
                .title(market.getTitle())
                .sales(market.getSales())
                .build());
    }

    public ResponseMarketDetailDTO getMarketDetail(Long id) {
        Market market = marketRepository.findById(id).orElse(null);

        if (market != null) {
            return ResponseMarketDetailDTO.builder()
                    .id(market.getId())
                    .member(market.getMember())
                    .title(market.getTitle())
                    .contents(market.getContents())
                    .sales(market.getSales())
                    .build();
        } else {
            return null;
        }
    }

    public void updateMarket(Long id, RequestMarketSaveDTO marketDTO, String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자가 없습니다."));

        Market market = marketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID를 가진 품목이 없습니다."));

        if (!market.getMember().getEmail().equals(memberEmail) && !member.getRole().equals(Role.ADMIN)) {
            throw new SecurityException("수정 권한이 없습니다.");
        }

        market = Market.builder()
                .title(marketDTO.getTitle())
                .contents(marketDTO.getContents())
                .sales(marketDTO.getSales())
                .build();

        marketRepository.save(market);
    }

    public void deleteMarket(Long id, String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자가 없습니다."));

        Market market = marketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID를 가진 품목이 없습니다."));

        if (!market.getMember().getEmail().equals(memberEmail) && !member.getRole().equals(Role.ADMIN)) {
            throw new SecurityException("삭제 권한이 없습니다.");
        }

        market.builder()
                .deletedAt(LocalDateTime.now())
                .build();

        marketRepository.save(market);
    }
}
