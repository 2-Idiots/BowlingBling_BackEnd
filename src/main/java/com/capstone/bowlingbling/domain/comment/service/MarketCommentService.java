package com.capstone.bowlingbling.domain.comment.service;

import com.capstone.bowlingbling.domain.comment.domain.MarketComment;
import com.capstone.bowlingbling.domain.comment.dto.request.CommentRequestDto;
import com.capstone.bowlingbling.domain.comment.dto.response.CommentResponseDto;
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
    private final MarketCommentRepository marketCommentRepository;
    private final MarketRepository marketRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public MarketCommentService(MarketCommentRepository marketCommentRepository, MarketRepository marketRepository, MemberRepository memberRepository){
        this.marketCommentRepository = marketCommentRepository;
        this.marketRepository = marketRepository;
        this.memberRepository = memberRepository;
    }
    @Transactional(readOnly = true)
    public Page<CommentResponseDto> getComments(Long marketId, Pageable pageable) {
        Market market = marketRepository.findById(marketId)
                .orElseThrow(() -> new IllegalArgumentException("커뮤니티를 찾을 수 없습니다."));

        return marketCommentRepository.findByMarket(market, pageable)
                .map(comment -> CommentResponseDto.builder()
                        .id(comment.getId())
                        .comments(comment.getComments())
                        .image(comment.getMember().getImage())
                        .memberName(comment.getMember().getNickname())
                        .build());
    }

    @Transactional
    public CommentResponseDto saveComment(Long marketId, CommentRequestDto requestDto, String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Market market = marketRepository.findById(marketId)
                .orElseThrow(() -> new IllegalArgumentException("커뮤니티를 찾을 수 없습니다."));

        MarketComment comment = MarketComment.builder()
                .member(member)
                .market(market)
                .comments(requestDto.getComments())
                .build();

        MarketComment savedComment = marketCommentRepository.save(comment);

        return CommentResponseDto.builder()
                .id(savedComment.getId())
                .comments(savedComment.getComments())
                .memberName(savedComment.getMember().getNickname())
                .build();
    }

    @Transactional
    public CommentResponseDto updateComment(Long marketId, Long commentId, CommentRequestDto requestDto, String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        marketRepository.findById(marketId).orElseThrow(() -> new IllegalArgumentException("커뮤니티를 찾을 수 없습니다."));
        MarketComment comment = marketCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!comment.getMember().equals(member) && !member.getRole().equals(Role.ADMIN)) {
            throw new IllegalArgumentException("댓글을 수정할 권한이 없습니다.");
        }

        comment = comment.toBuilder()
                .comments(requestDto.getComments())
                .build();

        MarketComment updatedComment = marketCommentRepository.save(comment);

        return CommentResponseDto.builder()
                .id(updatedComment.getId())
                .comments(updatedComment.getComments())
                .memberName(updatedComment.getMember().getNickname())
                .build();
    }

    @Transactional
    public void deleteComment(Long marketId, Long commentId, String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        marketRepository.findById(marketId).orElseThrow(() -> new IllegalArgumentException("커뮤니티를 찾을 수 없습니다."));
        MarketComment comment = marketCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!comment.getMember().equals(member) && !member.getRole().equals(Role.ADMIN)) {
            throw new IllegalArgumentException("댓글을 삭제할 권한이 없습니다.");
        }

        comment.markAsDeleted();
        marketCommentRepository.save(comment);
    }
}
