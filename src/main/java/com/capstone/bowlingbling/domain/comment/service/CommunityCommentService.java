package com.capstone.bowlingbling.domain.comment.service;

import com.capstone.bowlingbling.domain.comment.domain.CommunityComment;
import com.capstone.bowlingbling.domain.comment.dto.request.CommentRequestDto;
import com.capstone.bowlingbling.domain.comment.dto.response.CommentResponseDto;
import com.capstone.bowlingbling.domain.comment.repository.CommunityCommentRepository;
import com.capstone.bowlingbling.domain.community.domain.Community;
import com.capstone.bowlingbling.domain.community.repository.CommunityRepository;
import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.domain.member.repository.MemberRepository;
import com.capstone.bowlingbling.global.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommunityCommentService {

    private final CommunityCommentRepository communityCommentRepository;
    private final CommunityRepository communityRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public CommunityCommentService(CommunityCommentRepository communityCommentRepository, CommunityRepository communityRepository, MemberRepository memberRepository){
        this.communityCommentRepository = communityCommentRepository;
        this.communityRepository = communityRepository;
        this.memberRepository = memberRepository;
    }
    @Transactional(readOnly = true)
    public Page<CommentResponseDto> getComments(Long communityId, Pageable pageable) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new IllegalArgumentException("커뮤니티를 찾을 수 없습니다."));

        return communityCommentRepository.findByCommunity(community, pageable)
                .map(comment -> {
                    boolean isDeleted = comment.getDeletedAt() != null;
                    return CommentResponseDto.builder()
                            .id(comment.getId())
                            .comments(comment.getComments())
                            .memberName(comment.getMember().getNickname())
                            .image(comment.getMember().getImage())
                            .modifiedAt(comment.getModifiedAt())
                            .isDeleted(isDeleted)
                            .build();
                });
    }

    @Transactional
    public CommentResponseDto saveComment(Long communityId, CommentRequestDto requestDto, String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new IllegalArgumentException("커뮤니티를 찾을 수 없습니다."));

        CommunityComment comment = CommunityComment.builder()
                .member(member)
                .community(community)
                .comments(requestDto.getComments())
                .build();

        CommunityComment savedComment = communityCommentRepository.save(comment);

        return CommentResponseDto.builder()
                .id(savedComment.getId())
                .comments(savedComment.getComments())
                .memberName(savedComment.getMember().getNickname())
                .modifiedAt(savedComment.getModifiedAt())
                .isDeleted(false)
                .build();
    }

    @Transactional
    public CommentResponseDto updateComment(Long communityId, Long commentId, CommentRequestDto requestDto, String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        communityRepository.findById(communityId)
                .orElseThrow(() -> new IllegalArgumentException("커뮤니티를 찾을 수 없습니다."));
        CommunityComment comment = communityCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!comment.getMember().equals(member) && !member.getRole().equals(Role.ADMIN)) {
            throw new IllegalArgumentException("댓글을 수정할 권한이 없습니다.");
        }

        comment = comment.toBuilder()
                .comments(requestDto.getComments())
                .build();

        CommunityComment updatedComment = communityCommentRepository.save(comment);

        return CommentResponseDto.builder()
                .id(updatedComment.getId())
                .comments(updatedComment.getComments())
                .memberName(updatedComment.getMember().getNickname())
                .modifiedAt(updatedComment.getModifiedAt())
                .isDeleted(updatedComment.getDeletedAt() != null)
                .build();
    }

    @Transactional
    public void deleteComment(Long communityId, Long commentId, String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        communityRepository.findById(communityId)
                .orElseThrow(() -> new IllegalArgumentException("커뮤니티를 찾을 수 없습니다."));
        CommunityComment comment = communityCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!comment.getMember().equals(member) && !member.getRole().equals(Role.ADMIN)) {
            throw new IllegalArgumentException("댓글을 삭제할 권한이 없습니다.");
        }

        comment.markAsDeleted();
        communityCommentRepository.save(comment);
    }
}
