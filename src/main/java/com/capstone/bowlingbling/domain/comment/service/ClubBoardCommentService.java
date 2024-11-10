package com.capstone.bowlingbling.domain.comment.service;

import com.capstone.bowlingbling.domain.club.domain.ClubBoard;
import com.capstone.bowlingbling.domain.club.domain.ClubJoinList;
import com.capstone.bowlingbling.domain.club.dto.clubBoard.AuthorDto;
import com.capstone.bowlingbling.domain.club.repository.ClubBoardRepository;
import com.capstone.bowlingbling.domain.club.repository.ClubJoinListRepository;
import com.capstone.bowlingbling.domain.comment.domain.ClubBoardComment;
import com.capstone.bowlingbling.domain.comment.dto.request.CommentRequestDto;
import com.capstone.bowlingbling.domain.comment.dto.response.ClubBoardCommentResponseDto;
import com.capstone.bowlingbling.domain.comment.repository.ClubBoardCommentRepository;
import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.domain.member.repository.MemberRepository;
import com.capstone.bowlingbling.global.enums.ClubRole;
import com.capstone.bowlingbling.global.enums.Role;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClubBoardCommentService {

    private final ClubBoardCommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final ClubBoardRepository boardRepository;
    private final ClubJoinListRepository clubJoinListRepository;

    // 6. 댓글 목록 조회
    @Transactional
    public List<ClubBoardCommentResponseDto> getComments(Long clubId, Long postId) {
        List<ClubBoardComment> comments = commentRepository.findByClubBoardId(postId);

        return comments.stream()
                .map(comment -> ClubBoardCommentResponseDto.builder()
                        .id(comment.getId())
                        .postId(comment.getClubBoard().getId())
                        .content(comment.getComments())
                        .author(AuthorDto.builder()
                                .id(comment.getMember().getId())
                                .name(comment.getMember().getName())
                                .image(comment.getMember().getImage()) // 이미지 URL
                                .build())
                        .createdAt(comment.getCreatedAt())
                        .updatedAt(comment.getModifiedAt())
                        .build())
                .collect(Collectors.toList());
    }

    // 7. 댓글 작성
    @Transactional
    public void createComment(Long clubId, Long postId, String memberEmail, CommentRequestDto content) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));

        ClubBoard board = boardRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        ClubBoardComment comment = ClubBoardComment.builder()
                .clubBoard(board)
                .member(member)
                .comments(content.getComments())
                .build();

        commentRepository.save(comment);
    }

    // 8. 댓글 삭제
    @Transactional
    public void deleteComment(Long clubId, Long postId, Long commentId, String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));

        ClubBoardComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

        // 댓글이 해당 게시글과 일치하는지 확인
        if (!comment.getClubBoard().getId().equals(postId)) {
            throw new IllegalArgumentException("댓글이 해당 게시글에 속하지 않습니다.");
        }

        // 작성자 본인 또는 LEADER, MANAGER 권한 확인
        if (!comment.getMember().getEmail().equals(memberEmail) && !isAuthorizedForClub(clubId, member) && !member.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }

        commentRepository.deleteById(commentId);
    }

    private boolean isAuthorizedForClub(Long clubId, Member member) {
        // 클럽 ID와 멤버 ID로 ClubJoinList를 조회
        ClubJoinList clubJoinList = clubJoinListRepository.findByClub_IdAndMember_Id(clubId, member.getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 클럽에 멤버가 존재하지 않습니다."));

        // ClubRole이 LEADER, MANAGER 역할인 경우 true 반환
        return (clubJoinList.getClubRole() == ClubRole.LEADER ||
                clubJoinList.getClubRole() == ClubRole.MANAGER);
    }
}