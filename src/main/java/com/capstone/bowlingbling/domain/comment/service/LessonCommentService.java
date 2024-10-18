package com.capstone.bowlingbling.domain.comment.service;

import com.capstone.bowlingbling.domain.comment.domain.LessonComment;
import com.capstone.bowlingbling.domain.comment.dto.request.CommentRequestDto;
import com.capstone.bowlingbling.domain.comment.dto.response.CommentResponseDto;
import com.capstone.bowlingbling.domain.comment.repository.LessonCommentRepository;
import com.capstone.bowlingbling.domain.lessoninfo.domain.LessonInfo;
import com.capstone.bowlingbling.domain.lessoninfo.repository.LessonInfoRepository;
import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.domain.member.repository.MemberRepository;
import com.capstone.bowlingbling.global.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LessonCommentService {
    private final LessonCommentRepository lessonCommentRepository;
    private final LessonInfoRepository lessonInfoRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public LessonCommentService(LessonCommentRepository lessonCommentRepository, LessonInfoRepository lessonInfoRepository, MemberRepository memberRepository){
        this.lessonCommentRepository = lessonCommentRepository;
        this.lessonInfoRepository = lessonInfoRepository;
        this.memberRepository = memberRepository;
    }
    @Transactional(readOnly = true)
    public Page<CommentResponseDto> getComments(Long lessonId, Pageable pageable) {
        LessonInfo lesson = lessonInfoRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("레슨을 찾을 수 없습니다."));

        return lessonCommentRepository.findByLessonInfo(lesson, pageable)
                .map(comment -> CommentResponseDto.builder()
                        .id(comment.getId())
                        .comments(comment.getConmments())
                        .memberName(comment.getMember().getNickname())
                        .modifiedAt(comment.getModifiedAt())
                        .isDeleted(comment.getDeletedAt() != null)
                        .build());
    }

    @Transactional
    public void saveComment(Long lessonId, CommentRequestDto requestDto, String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        LessonInfo lesson = lessonInfoRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("커뮤니티를 찾을 수 없습니다."));

        LessonComment comment = LessonComment.builder()
                .member(member)
                .lesson(lesson)
                .conmments(requestDto.getComments())
                .build();

        lessonCommentRepository.save(comment);
    }

    @Transactional
    public void updateComment(Long marketId, Long commentId, CommentRequestDto requestDto, String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        lessonInfoRepository.findById(marketId)
                .orElseThrow(() -> new IllegalArgumentException("커뮤니티를 찾을 수 없습니다."));
        LessonComment comment = lessonCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!comment.getMember().equals(member) && !member.getRole().equals(Role.ADMIN)) {
            throw new IllegalArgumentException("댓글을 수정할 권한이 없습니다.");
        }

        lessonCommentRepository.updateComment(commentId, requestDto.getComments());
    }

    @Transactional
    public void deleteComment(Long marketId, Long commentId, String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        lessonInfoRepository.findById(marketId)
                .orElseThrow(() -> new IllegalArgumentException("커뮤니티를 찾을 수 없습니다."));
        LessonComment comment = lessonCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!comment.getMember().equals(member) && !member.getRole().equals(Role.ADMIN)) {
            throw new IllegalArgumentException("댓글을 삭제할 권한이 없습니다.");
        }

        comment.markAsDeleted();
        lessonCommentRepository.save(comment);
    }
}