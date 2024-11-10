package com.capstone.bowlingbling.domain.member.service;

import com.capstone.bowlingbling.domain.club.domain.ClubJoinList;
import com.capstone.bowlingbling.domain.club.dto.response.ClubDetailResponseDto;
import com.capstone.bowlingbling.domain.club.dto.response.ClubJoinListResponseDto;
import com.capstone.bowlingbling.domain.club.dto.response.ClubListResponseDto;
import com.capstone.bowlingbling.domain.club.dto.response.ClubMemberResponseDto;
import com.capstone.bowlingbling.domain.club.repository.ClubJoinListRepository;
import com.capstone.bowlingbling.domain.club.repository.ClubRepository;
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
import com.capstone.bowlingbling.global.enums.ClubRole;
import com.capstone.bowlingbling.global.enums.RequestStatus;
import com.capstone.bowlingbling.global.enums.Role;
import com.capstone.bowlingbling.global.enums.TeacherStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final TeacherRequestRepository teacherRequestRepository;
    private final S3ImageService s3ImageService;
    private final LikedLessonRepository likedLessonRepository;
    private final CenterCommentRepository centerCommentRepository;
    private final LessonCommentRepository lessonCommentRepository;
    private final ClubJoinListRepository clubJoinListRepository;

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
                .myaver(member.getMyaver())
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
                request.getMyaver() != null ? request.getMyaver() : member.getMyaver(),
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

    public List<ClubListResponseDto> getMyClubs(String memberEmail) {
        List<ClubJoinList> clubJoinLists = clubJoinListRepository.findClubsByMemberEmail(memberEmail);
        return clubJoinLists.stream()
                .map(clubList -> {
                    int activeMemberCount = clubJoinListRepository.countByClubIdAndStatus(clubList.getClub().getId(), RequestStatus.ACTIVE);
                    return ClubListResponseDto.builder()
                            .id(clubList.getClub().getId())
                            .name(clubList.getClub().getClubName())
                            .description(clubList.getClub().getDescription())
                            .location(clubList.getClub().getLocation())
                            .memberCount(activeMemberCount)
                            .maxMembers(clubList.getClub().getMaxMembers())
                            .averageScore(clubList.getClub().getAverageScore())
                            .meetingDays(clubList.getClub().getMeetingDays())
                            .images(clubList.getClub().getImages())
                            .leader(new ClubMemberResponseDto(clubList.getClub().getLeader()))
                            .isRecruiting(clubList.getClub().isRecruiting())
                            .category(clubList.getClub().getCategory())
                            .requirements(clubList.getClub().getRequirements())
                            .monthlyFee(clubList.getClub().getMonthlyFee())
                            .establishedAt(clubList.getClub().getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                            .build();
                })
                .collect(Collectors.toList());
    }

    // 사용자가 매니징하는 클럽 리스트 가져오기
    public List<ClubListResponseDto> getManagingClubs(String memberEmail) {
        List<ClubRole> roles = Arrays.asList(ClubRole.LEADER, ClubRole.MANAGER);
        List<ClubJoinList> managingClubs = clubJoinListRepository.findManagingClubsByMemberEmail(memberEmail, roles);

        if (managingClubs.isEmpty()) {
            throw new IllegalArgumentException("관리 중인 클럽이 없습니다.");
        }

        return managingClubs.stream()
                .map(clubList -> {
                    int activeMemberCount = clubJoinListRepository.countByClubIdAndStatus(clubList.getClub().getId(), RequestStatus.ACTIVE);
                    return ClubListResponseDto.builder()
                            .id(clubList.getClub().getId())
                            .name(clubList.getClub().getClubName())
                            .description(clubList.getClub().getDescription())
                            .location(clubList.getClub().getLocation())
                            .memberCount(activeMemberCount)
                            .maxMembers(clubList.getClub().getMaxMembers())
                            .averageScore(clubList.getClub().getAverageScore())
                            .meetingDays(clubList.getClub().getMeetingDays())
                            .images(clubList.getClub().getImages())
                            .leader(new ClubMemberResponseDto(clubList.getClub().getLeader()))
                            .isRecruiting(clubList.getClub().isRecruiting())
                            .category(clubList.getClub().getCategory())
                            .requirements(clubList.getClub().getRequirements())
                            .monthlyFee(clubList.getClub().getMonthlyFee())
                            .establishedAt(clubList.getClub().getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                            .build();
                })
                .collect(Collectors.toList());
    }

    // 사용자가 신청 대기 중인 클럽 리스트 가져오기
    public List<ClubJoinListResponseDto> getMyApplications(String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<ClubJoinList> pendingApplications = clubJoinListRepository.findPendingApplicationsByMemberEmail(memberEmail);
        return pendingApplications.stream()
                .map(clubJoinList -> ClubJoinListResponseDto.builder()
                        .id(clubJoinList.getClub().getId())
                        .clubId(clubJoinList.getClub().getId())
                        .averageScore(clubJoinList.getClub().getAverageScore())
                        .experience(clubJoinList.getExperience())
                        .motivation(clubJoinList.getMotivation())
                        .availability(clubJoinList.getAvailability())
                        .status(clubJoinList.getStatus())
                        .createdAt(clubJoinList.getCreatedAt())
                        .user(new ClubMemberResponseDto(member))
                        .build())
                .collect(Collectors.toList());
    }
}
