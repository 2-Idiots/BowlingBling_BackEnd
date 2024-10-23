package com.capstone.bowlingbling.domain.comment.service;

import com.capstone.bowlingbling.domain.center.domain.Center;
import com.capstone.bowlingbling.domain.center.repository.CenterRepository;
import com.capstone.bowlingbling.domain.comment.domain.CenterComment;
import com.capstone.bowlingbling.domain.comment.dto.request.CommentRequestDto;
import com.capstone.bowlingbling.domain.comment.dto.response.CommentResponseDto;
import com.capstone.bowlingbling.domain.comment.repository.CenterCommentRepository;
import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.domain.member.repository.MemberRepository;
import com.capstone.bowlingbling.global.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CenterCommentService {
    private final CenterCommentRepository centerCommentRepository;
    private final CenterRepository centerRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public CenterCommentService(CenterCommentRepository centerCommentRepository, CenterRepository centerRepository, MemberRepository memberRepository){
        this.centerCommentRepository = centerCommentRepository;
        this.centerRepository = centerRepository;
        this.memberRepository = memberRepository;
    }
    @Transactional(readOnly = true)
    public Page<CommentResponseDto> getAllComments(Long centerId, Pageable pageable) {
        Center center = centerRepository.findById(centerId)
                .orElseThrow(() -> new IllegalArgumentException("레슨을 찾을 수 없습니다."));

        return centerCommentRepository.findByCenter(center, pageable)
                .map(comment -> CommentResponseDto.builder()
                        .id(comment.getId())
                        .comments(comment.getComments())
                        .memberName(comment.getMember().getNickname())
                        .image(comment.getMember().getImage())
                        .modifiedAt(comment.getModifiedAt())
                        .isDeleted(comment.getDeletedAt() != null)
                        .build());
    }

    @Transactional
    public void saveComment(Long centerId, CommentRequestDto requestDto, String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Center center = centerRepository.findById(centerId)
                .orElseThrow(() -> new IllegalArgumentException("커뮤니티를 찾을 수 없습니다."));

        CenterComment comment = CenterComment.builder()
                .member(member)
                .center(center)
                .comments(requestDto.getComments())
                .build();

        centerCommentRepository.save(comment);
    }

    @Transactional
    public void updateComment(Long marketId, Long commentId, CommentRequestDto requestDto, String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        centerRepository.findById(marketId)
                .orElseThrow(() -> new IllegalArgumentException("커뮤니티를 찾을 수 없습니다."));
        CenterComment comment = centerCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!comment.getMember().equals(member) && !member.getRole().equals(Role.ADMIN)) {
            throw new IllegalArgumentException("댓글을 수정할 권한이 없습니다.");
        }

        centerCommentRepository.updateComment(commentId, requestDto.getComments());
    }

    @Transactional
    public void deleteComment(Long marketId, Long commentId, String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        centerRepository.findById(marketId)
                .orElseThrow(() -> new IllegalArgumentException("커뮤니티를 찾을 수 없습니다."));
        CenterComment comment = centerCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!comment.getMember().equals(member) && !member.getRole().equals(Role.ADMIN)) {
            throw new IllegalArgumentException("댓글을 삭제할 권한이 없습니다.");
        }

        comment.markAsDeleted();
        centerCommentRepository.save(comment);
    }
}