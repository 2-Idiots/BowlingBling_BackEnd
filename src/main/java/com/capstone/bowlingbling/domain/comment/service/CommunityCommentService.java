package com.capstone.bowlingbling.domain.comment.service;

import com.capstone.bowlingbling.domain.comment.domain.CommunityComment;
import com.capstone.bowlingbling.domain.comment.dto.request.CommunityCommentRequestDto;
import com.capstone.bowlingbling.domain.comment.dto.response.CommunityCommentResponseDto;
import com.capstone.bowlingbling.domain.comment.repository.CommunityCommentRepository;
import com.capstone.bowlingbling.domain.community.domain.Community;
import com.capstone.bowlingbling.domain.community.repository.CommunityRepository;
import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.domain.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CommunityCommentService {
    private CommunityCommentRepository communityCommentRepository;
    private CommunityRepository communityRepository;
    private MemberRepository memberRepository;

    @Autowired
    public CommunityCommentService(CommunityCommentRepository communityCommentRepository, CommunityRepository communityRepository, MemberRepository memberRepository){
        this.communityCommentRepository = communityCommentRepository;
        this.communityRepository = communityRepository;
        this.memberRepository = memberRepository;
    }
    @Transactional(readOnly = true)
    public Page<CommunityCommentResponseDto> getComments(Long communityId, Pageable pageable) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new IllegalArgumentException("커뮤니티를 찾을 수 없습니다."));

        return communityCommentRepository.findByCommunity(community, pageable)
                .map(comment -> CommunityCommentResponseDto.builder()
                        .id(comment.getId())
                        .comments(comment.getConmments())
                        .memberName(comment.getMember().getNickname())
                        .communityTitle(comment.getCommunity().getTitle())
                        .build());
    }

    @Transactional
    public CommunityCommentResponseDto saveComment(Long communityId, CommunityCommentRequestDto requestDto, String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new IllegalArgumentException("커뮤니티를 찾을 수 없습니다."));

        CommunityComment comment = CommunityComment.builder()
                .member(member)
                .community(community)
                .conmments(requestDto.getComments())
                .build();

        CommunityComment savedComment = communityCommentRepository.save(comment);

        return CommunityCommentResponseDto.builder()
                .id(savedComment.getId())
                .comments(savedComment.getConmments())
                .memberName(savedComment.getMember().getNickname())
                .communityTitle(savedComment.getCommunity().getTitle())
                .build();
    }

    @Transactional
    public CommunityCommentResponseDto updateComment(Long communityId, Long commentId, CommunityCommentRequestDto requestDto, String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new IllegalArgumentException("커뮤니티를 찾을 수 없습니다."));
        CommunityComment comment = communityCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!comment.getMember().equals(member)) {
            throw new IllegalArgumentException("댓글을 수정할 권한이 없습니다.");
        }

        comment = comment.toBuilder()
                .conmments(requestDto.getComments())
                .build();

        CommunityComment updatedComment = communityCommentRepository.save(comment);

        return CommunityCommentResponseDto.builder()
                .id(updatedComment.getId())
                .comments(updatedComment.getConmments())
                .memberName(updatedComment.getMember().getNickname())
                .communityTitle(updatedComment.getCommunity().getTitle())
                .build();
    }

    @Transactional
    public void deleteComment(Long communityId, Long commentId, String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new IllegalArgumentException("커뮤니티를 찾을 수 없습니다."));
        CommunityComment comment = communityCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!comment.getMember().equals(member)) {
            throw new IllegalArgumentException("댓글을 삭제할 권한이 없습니다.");
        }

        comment.markAsDeleted();
        communityCommentRepository.save(comment);
    }
}
