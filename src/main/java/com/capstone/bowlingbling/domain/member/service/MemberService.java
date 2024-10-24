package com.capstone.bowlingbling.domain.member.service;

import com.capstone.bowlingbling.domain.comment.domain.CenterComment;
import com.capstone.bowlingbling.domain.comment.domain.LessonComment;
import com.capstone.bowlingbling.domain.comment.dto.response.MyCommentResponseDto;
import com.capstone.bowlingbling.domain.comment.repository.CenterCommentRepository;
import com.capstone.bowlingbling.domain.comment.repository.LessonCommentRepository;
import com.capstone.bowlingbling.domain.image.service.S3ImageService;
import com.capstone.bowlingbling.domain.lessoninfo.domain.LessonInfo;
import com.capstone.bowlingbling.domain.lessoninfo.domain.LikedLesson;
import com.capstone.bowlingbling.domain.lessoninfo.dto.response.LessonInfoResponseDto;
import com.capstone.bowlingbling.domain.lessoninfo.repository.LikedLessonRepository;
import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.domain.member.domain.TeacherRequest;
import com.capstone.bowlingbling.domain.member.dto.MemberInfoResponseDto;
import com.capstone.bowlingbling.domain.member.dto.MemberProfileUpdateRequest;
import com.capstone.bowlingbling.domain.member.dto.TeacherRequestDto;
import com.capstone.bowlingbling.domain.member.repository.MemberRepository;
import com.capstone.bowlingbling.domain.member.repository.TeacherRequestRepository;
import com.capstone.bowlingbling.global.enums.Role;
import com.capstone.bowlingbling.global.enums.TeacherStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final TeacherRequestRepository teacherRequestRepository;
    private final S3ImageService s3ImageService;
    private final LikedLessonRepository likedLessonRepository;
    private final CenterCommentRepository centerCommentRepository;
    private final LessonCommentRepository lessonCommentRepository;

    public MemberService(MemberRepository memberRepository,
                         TeacherRequestRepository teacherRequestRepository,
                         S3ImageService s3ImageService,
                         LikedLessonRepository likedLessonRepository,
                         CenterCommentRepository centerCommentRepository,
                         LessonCommentRepository lessonCommentRepository) {
        this.memberRepository = memberRepository;
        this.teacherRequestRepository = teacherRequestRepository;
        this.s3ImageService = s3ImageService;
        this.likedLessonRepository = likedLessonRepository;
        this.centerCommentRepository = centerCommentRepository;
        this.lessonCommentRepository = lessonCommentRepository;
    }

    @Transactional(readOnly = true)
    public MemberInfoResponseDto getMemberInfo(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 회원을 찾을 수 없습니다."));

        return MemberInfoResponseDto.builder()
                .image(member.getImage())
                .email(member.getEmail())
                .name(member.getName())
                .nickname(member.getNickname())
                .city(member.getCity())
                .age(member.getAge())
                .phonenum(member.getPhonenum())
                .introduction(member.getIntroduction())
                .sex(member.getSex())
                .role(member.getRole())
                .socialType(member.getSocialType())
                .build();
    }

    public void logout(String email) {
        memberRepository.deleteRefreshTokenByEmail(email);
    }

    @Transactional
    public void updateProfile(MemberProfileUpdateRequest request, String email, MultipartFile files) throws IOException {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자가 없습니다."));

        if (!member.getEmail().equals(request.getEmail()) && memberRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("해당 이메일은 이미 사용 중입니다.");
        }

        if (!member.getEmail().equals(email) && !member.getRole().equals(Role.ADMIN)) {
            throw new SecurityException("수정 권한이 없습니다.");
        }

        String imageUrls = files != null ? s3ImageService.upload(files) : member.getImage();

        // repository를 통해 업데이트
        memberRepository.updateProfile(
                request.getName() != null ? request.getName() : member.getName(),
                request.getNickname() != null ? request.getNickname() : member.getNickname(),
                request.getEmail() != null ? request.getEmail() : member.getEmail(),
                imageUrls != null ? imageUrls : member.getImage(),
                request.getPhonenum() != null ? request.getPhonenum() : member.getPhonenum(),
                request.getCity() != null ? request.getCity() : member.getCity(),
                request.getSex() != null ? request.getSex() : member.getSex(),
                request.getAge() != null ? request.getAge() : member.getAge(),
                request.getIntroduction() != null ? request.getIntroduction() : member.getIntroduction(),
                email  // 현재 로그인된 사용자의 이메일
        );
    }

    @Transactional
    public TeacherRequest requestTeacherRole(TeacherRequestDto request, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자가 없습니다."));

        TeacherRequest teacherRequest = TeacherRequest.builder()
                .member(member)
                .specialty(request.getSpecialty())
                .bio(request.getBio())
                .status(TeacherStatus.PENDING)
                .build();

        return teacherRequestRepository.save(teacherRequest);
    }

    @Transactional(readOnly = true)
    public Page<TeacherRequest> getPendingTeacherRequests(Pageable pageable) {
        return teacherRequestRepository.findAllByStatus(TeacherStatus.PENDING, pageable);
    }

    @Transactional
    public void approveTeacherRequest(Long requestId) {
        TeacherRequest teacherRequest = teacherRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("해당 요청을 찾을 수 없습니다."));

        Member member = teacherRequest.getMember();
        member = member.toBuilder()
                .role(Role.TEACHER)
                .build();

        teacherRequest = teacherRequest.toBuilder()
                .status(TeacherStatus.APPROVED)
                .build();

        memberRepository.save(member);
        teacherRequestRepository.save(teacherRequest);
    }

    @Transactional(readOnly = true)
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자가 없습니다."));
    }

    public List<LessonInfoResponseDto> getMyLikedLessons(String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // LikedLessonRepository를 이용해 회원이 찜한 레슨 가져오기
        List<LessonInfo> likedLessons = likedLessonRepository.findByMember(member).stream()
                .map(LikedLesson::getLessonInfo) // LikedLesson에서 LessonInfo 추출
                .collect(Collectors.toList());

        return likedLessons.stream()
                .map(lesson -> LessonInfoResponseDto.builder()
                        .id(lesson.getId())
                        .title(lesson.getTitle())
                        .introduction(lesson.getIntro())
                        .teacherName(lesson.getTeacherName())
                        .contents(lesson.getContents())
                        .location(lesson.getAddress())
                        .qualifications(lesson.getQualifications())
                        .lat(lesson.getLat())
                        .lng(lesson.getLng())
                        .place(lesson.getPlace())
                        .category(lesson.getCategory())
                        .price(lesson.getPrice())
                        .hasFreeParking(lesson.getHasFreeParking())
                        .careerHistory(lesson.getCareerHistory())
                        .program(lesson.getProgram())
                        .operatingHours(lesson.getOperatingHours())
                        .imageUrls(lesson.getImages())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MyCommentResponseDto> getAllUserComments(String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<LessonComment> lessonComments = lessonCommentRepository.findByMemberAndDeletedAtIsNull(member);
        List<CenterComment> centerComments = centerCommentRepository.findByMemberAndDeletedAtIsNull(member);

        List<MyCommentResponseDto> responseDtos = new ArrayList<>();

        // LessonComments를 DTO로 변환, commentType은 'LESSON'
        lessonComments.forEach(comment -> {
            responseDtos.add(MyCommentResponseDto.builder()
                    .id(comment.getId())
                    .comments(comment.getComments())
                    .memberName(comment.getMember().getNickname())
                    .image(comment.getMember().getImage())
                    .modifiedAt(comment.getModifiedAt())
                    .isDeleted(comment.getDeletedAt() != null)
                    .commentType("LESSON")
                    .commentId(comment.getLesson().getId()) // 레슨 ID 추가
                    .build());
        });

        // CenterComments를 DTO로 변환, commentType은 'CENTER'
        centerComments.forEach(comment -> {
            responseDtos.add(MyCommentResponseDto.builder()
                    .id(comment.getId())
                    .comments(comment.getComments())
                    .memberName(comment.getMember().getNickname())
                    .image(comment.getMember().getImage())
                    .modifiedAt(comment.getModifiedAt())
                    .isDeleted(comment.getDeletedAt() != null)
                    .commentType("CENTER")
                    .commentId(comment.getCenter().getId()) // 센터 ID 추가
                    .build());
        });

        return responseDtos;
    }
}
