package com.capstone.bowlingbling.domain.comment.service;

import com.capstone.bowlingbling.domain.comment.domain.MarketComment;
import com.capstone.bowlingbling.domain.comment.dto.request.MarketCommentRequestDto;
import com.capstone.bowlingbling.domain.comment.dto.response.MarketCommentResponseDto;
import com.capstone.bowlingbling.domain.comment.repository.MarketCommentRepository;
import com.capstone.bowlingbling.domain.market.domain.Market;
import com.capstone.bowlingbling.domain.market.repository.MarketRepository;
import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.domain.member.repository.MemberRepository;
import com.capstone.bowlingbling.global.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MarketCommentService {
    private MarketCommentRepository marketCommentRepository;
    private MarketRepository marketRepository;
    private MemberRepository memberRepository;

    @Autowired
    public MarketCommentService(MarketCommentRepository marketCommentRepository, MarketRepository marketRepository, MemberRepository memberRepository){
        this.marketCommentRepository = marketCommentRepository;
        this.marketRepository = marketRepository;
        this.memberRepository = memberRepository;
    }
    @Transactional(readOnly = true)
    public Page<MarketCommentResponseDto> getComments(Long marketId, Pageable pageable) {
        Market market = marketRepository.findById(marketId)
                .orElseThrow(() -> new IllegalArgumentException("커뮤니티를 찾을 수 없습니다."));

        return marketCommentRepository.findByMarket(market, pageable)
                .map(comment -> MarketCommentResponseDto.builder()
                        .id(comment.getId())
                        .comments(comment.getConmments())
                        .memberName(comment.getMember().getNickname())
                        .marketTitle(comment.getMarket().getTitle())
                        .build());
    }

    @Transactional
    public MarketCommentResponseDto saveComment(Long marketId, MarketCommentRequestDto requestDto, String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Market market = marketRepository.findById(marketId)
                .orElseThrow(() -> new IllegalArgumentException("커뮤니티를 찾을 수 없습니다."));

        MarketComment comment = MarketComment.builder()
                .member(member)
                .market(market)
                .conmments(requestDto.getComments())
                .build();

        MarketComment savedComment = marketCommentRepository.save(comment);

        return MarketCommentResponseDto.builder()
                .id(savedComment.getId())
                .comments(savedComment.getConmments())
                .memberName(savedComment.getMember().getNickname())
                .marketTitle(savedComment.getMarket().getTitle())
                .build();
    }

    @Transactional
    public MarketCommentResponseDto updateComment(Long marketId, Long commentId, MarketCommentRequestDto requestDto, String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Market market = marketRepository.findById(marketId)
                .orElseThrow(() -> new IllegalArgumentException("커뮤니티를 찾을 수 없습니다."));
        MarketComment comment = marketCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!comment.getMember().equals(member) && !member.getRole().equals(Role.ADMIN)) {
            throw new IllegalArgumentException("댓글을 수정할 권한이 없습니다.");
        }

        comment = comment.toBuilder()
                .conmments(requestDto.getComments())
                .build();

        MarketComment updatedComment = marketCommentRepository.save(comment);

        return MarketCommentResponseDto.builder()
                .id(updatedComment.getId())
                .comments(updatedComment.getConmments())
                .memberName(updatedComment.getMember().getNickname())
                .marketTitle(updatedComment.getMarket().getTitle())
                .build();
    }

    @Transactional
    public void deleteComment(Long marketId, Long commentId, String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Market market = marketRepository.findById(marketId)
                .orElseThrow(() -> new IllegalArgumentException("커뮤니티를 찾을 수 없습니다."));
        MarketComment comment = marketCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!comment.getMember().equals(member) && !member.getRole().equals(Role.ADMIN)) {
            throw new IllegalArgumentException("댓글을 삭제할 권한이 없습니다.");
        }

        comment.markAsDeleted();
        marketCommentRepository.save(comment);
    }
}
