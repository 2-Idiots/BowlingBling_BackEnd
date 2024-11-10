package com.capstone.bowlingbling.domain.club.service;

import com.capstone.bowlingbling.domain.club.domain.ClubBoard;
import com.capstone.bowlingbling.domain.club.domain.ClubBoardFile;
import com.capstone.bowlingbling.domain.club.dto.clubBoard.*;
import com.capstone.bowlingbling.domain.club.repository.ClubBoardFileRepository;
import com.capstone.bowlingbling.domain.club.repository.ClubBoardRepository;
import com.capstone.bowlingbling.domain.image.service.S3ImageService;
import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.domain.member.repository.MemberRepository;
import com.capstone.bowlingbling.global.enums.ClubCategory;
import com.capstone.bowlingbling.global.enums.Role;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClubBoardService {

    private final ClubBoardRepository boardRepository;
    private final S3ImageService s3ImageService;
    private final MemberRepository memberRepository;
    private final ClubBoardFileRepository clubBoardFileRepository;

    @Transactional(readOnly = true)
    public ClubBoardListResponseDto getPostList(Long clubId, ClubCategory category, String searchType, String keyword, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        Specification<ClubBoard> spec = buildSpecification(clubId, category, searchType, keyword);

        Page<ClubBoard> postPage = boardRepository.findAll(spec, pageRequest);

        List<ClubBoardDetailDto> postDtos = postPage.getContent().stream()
                .map(this::convertToPostDto)
                .collect(Collectors.toList());

        return ClubBoardListResponseDto.builder()
                .posts(postDtos)
                .totalElements(postPage.getTotalElements())
                .totalPages(postPage.getTotalPages())
                .size(size)
                .number(page)
                .build();
    }

    @Transactional(readOnly = true)
    public ClubBoardDetailDto getPostDetail(Long clubId, Long postId) {
        ClubBoard post = boardRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
        return convertToPostDto(post);
    }

    @Transactional
    public void createPost(Long clubId, ClubBoardCreateDto request, String memberEmail, List<MultipartFile> attachments) throws IOException {
        // 작성자 정보 조회
        Member author = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));

        // ClubBoard 엔티티 생성
        ClubBoard clubBoard = ClubBoard.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .clubCategory(request.getCategory())
                .author(author)
                .isPinned(request.getIsPinned())
                .build();

        // 첨부 파일 처리
        if (attachments != null && !attachments.isEmpty() && !attachments.get(0).isEmpty()) {
            List<String> attachmentUrls = s3ImageService.uploadClubMultiple(attachments.toArray(new MultipartFile[0]));

            for (int i = 0; i < attachments.size(); i++) {
                MultipartFile file = attachments.get(i);
                String fileUrl = attachmentUrls.get(i);

                // ClubBoardFile 엔티티 생성
                ClubBoardFile clubBoardFile = ClubBoardFile.builder()
                        .clubBoard(clubBoard)
                        .filename(file.getOriginalFilename())
                        .fileUrl(fileUrl)
                        .fileSize(file.getSize())
                        .mimeType(file.getContentType())
                        .build();

                clubBoard.getAttachments().add(clubBoardFile);  // ClubBoard에 첨부 파일 추가
            }
        }

        // ClubBoard 엔티티 저장 (첨부 파일 정보도 함께 저장됨)
        boardRepository.save(clubBoard);
    }

    @Transactional
    public ClubBoardDetailDto updatePost(Long clubId, Long postId, String memberEmail, ClubBoardCreateDto request, List<Long> keepAttachments, List<MultipartFile> attachments) throws IOException {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));

        ClubBoard post = boardRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        // 권한 검사
        if (isAuthorized(member, post)) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }

        // 게시글 내용 수정
        boardRepository.updatePost(
                postId,
                request.getTitle(),
                request.getContent(),
                request.getCategory(),
                request.getIsPinned()
        );

        if (keepAttachments != null) {
            // keepAttachments에 포함되지 않은 파일을 삭제
            List<ClubBoardFile> filesToRemove = post.getAttachments().stream()
                    .filter(file -> !keepAttachments.contains(file.getId())) // keepAttachments에 포함되지 않은 파일 삭제
                    .collect(Collectors.toList());

            // 기존 첨부파일 삭제
            for (ClubBoardFile fileToRemove : filesToRemove) {
                // S3에서 파일 삭제
                s3ImageService.deleteFile(fileToRemove.getFileUrl());

                // ClubBoardFile 엔티티 삭제
                clubBoardFileRepository.delete(fileToRemove);
            }
        }
        // 새로운 첨부파일 처리
        if (attachments != null && !attachments.isEmpty()) {
            List<String> newAttachments = s3ImageService.uploadMultiple(attachments.toArray(new MultipartFile[0]));

            // 새로운 첨부파일 저장
            for (String fileUrl : newAttachments) {
                ClubBoardFile newFile = ClubBoardFile.builder()
                        .clubBoard(post)
                        .fileUrl(fileUrl)
                        .filename(fileUrl)  // 예시로 fileUrl을 filename으로 사용
                        .fileSize(0L)        // 실제 크기 및 MIME 타입 처리 필요
                        .mimeType("application/octet-stream") // 실제 MIME 타입 처리 필요
                        .build();

                clubBoardFileRepository.save(newFile);
            }
        }

        return convertToPostDto(post);
    }

    @Transactional
    public void deletePost(Long clubId, Long postId, String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));

        ClubBoard post = boardRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        // 권한 검사
        if (isAuthorized(member, post)) {
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }

        List<ClubBoardFile> files = post.getAttachments();
        if (files != null && !files.isEmpty()) {
            for (ClubBoardFile file : files) {
                // S3에서 파일 삭제
                s3ImageService.deleteFile(file.getFileUrl());

                // ClubBoardFile 엔티티 삭제
                clubBoardFileRepository.delete(file);
            }
        }

        boardRepository.delete(post);
    }

    @Transactional(readOnly = true)
    public List<ClubBoardDetailDto> getPinnedPosts(Long clubId) {
        return boardRepository.findByClubIdAndIsPinnedTrue(clubId).stream()
                .map(this::convertToPostDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> downloadAttachment(Long clubId, Long postId, Long attachmentId) throws IOException {
        // 게시글 조회
        ClubBoard post = boardRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        // 첨부파일 조회 (첨부파일 ID와 게시글 ID를 기준으로 조회)
        ClubBoardFile attachment = clubBoardFileRepository.findById(attachmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 첨부파일이 존재하지 않습니다."));

        // 첨부파일이 해당 게시글과 연결되어 있는지 확인
        if (!attachment.getClubBoard().getId().equals(postId)) {
            throw new IllegalArgumentException("첨부파일과 게시글이 일치하지 않습니다.");
        }

        // 파일 다운로드 요청을 FileDownloadService로 위임
        return s3ImageService.downloadFileFromS3(attachment.getFileUrl(), attachment.getFilename());
    }

    private boolean isAuthorized(Member member, ClubBoard post) {
        return !member.getId().equals(post.getAuthor().getId()) && !member.getRole().equals(Role.ADMIN);
    }

    private Specification<ClubBoard> buildSpecification(Long clubId, ClubCategory category, String searchType, String keyword) {
        return (root, query, criteriaBuilder) -> {
            Predicate basePredicate = criteriaBuilder.equal(root.get("clubId"), clubId);

            Predicate categoryPredicate = category != null
                    ? criteriaBuilder.equal(root.get("category"), category)
                    : criteriaBuilder.conjunction();

            Predicate keywordPredicate = keyword != null && searchType != null
                    ? switch (searchType) {
                case "title" -> criteriaBuilder.like(root.get("title"), "%" + keyword + "%");
                case "content" -> criteriaBuilder.like(root.get("content"), "%" + keyword + "%");
                case "author" -> criteriaBuilder.like(root.get("author").get("name"), "%" + keyword + "%");
                default -> criteriaBuilder.conjunction();
            }
                    : criteriaBuilder.conjunction();

            return criteriaBuilder.and(basePredicate, categoryPredicate, keywordPredicate);
        };
    }

    private ClubBoardDetailDto convertToPostDto(ClubBoard post) {
        return ClubBoardDetailDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .category(post.getClubCategory())
                .author(AuthorDto.builder()
                        .id(post.getAuthor().getId())
                        .name(post.getAuthor().getName())
                        .image(post.getAuthor().getImage())
                        .build())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getModifiedAt())
                .viewCount(post.getViewCount())
                .commentCount(post.getCommentCount())
                .isPinned(post.getIsPinned())
                .attachments(post.getAttachments().stream()
                        .map(att -> AttachmentDto.builder()
                                .id(att.getId())
                                .filename(att.getFilename())
                                .fileUrl(att.getFileUrl())
                                .fileSize(att.getFileSize())
                                .mimeType(att.getMimeType())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}