package com.capstone.bowlingbling.domain.club.service;

import com.capstone.bowlingbling.domain.club.domain.Club;
import com.capstone.bowlingbling.domain.club.domain.ClubJoinList;
import com.capstone.bowlingbling.domain.club.dto.request.*;
import com.capstone.bowlingbling.domain.club.dto.response.*;
import com.capstone.bowlingbling.domain.club.repository.ClubJoinListRepository;
import com.capstone.bowlingbling.domain.club.repository.ClubRepository;
import com.capstone.bowlingbling.domain.image.service.S3ImageService;
import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.domain.member.repository.MemberRepository;
import com.capstone.bowlingbling.global.enums.ClubRole;
import com.capstone.bowlingbling.global.enums.RequestStatus;
import com.capstone.bowlingbling.global.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
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
public class ClubService {

    private final ClubRepository clubRepository;
    private final MemberRepository memberRepository;
    private final S3ImageService s3ImageService;
    private final ClubJoinListRepository clubJoinListRepository;

    @Transactional
    public void createClub(ClubCreateDto request, String memberEmail, List<MultipartFile> files) throws IOException {
        if (files != null && files.size() > 5) {
            throw new IllegalArgumentException("이미지는 최대 5개까지 등록할 수 있습니다.");
        }
        // 현재 로그인 사용자를 통해 회장 정보 가져오기
        Member leader = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));

        // S3에 파일 업로드 및 URL 리스트로 저장
        List<String> imageUrls = s3ImageService.uploadMultiple(files.toArray(new MultipartFile[0]));

        // Club 엔티티 생성
        Club club = Club.builder()
                .clubName(request.getName())
                .description(request.getDescription())
                .location(request.getLocation())
                .maxMembers(request.getMaxMembers())
                .averageScore(request.getAverageScore())
                .category(request.getCategory())
                .requirements(request.getRequirements())
                .monthlyFee(request.getMonthlyFee())
                .isRecruiting(true)
                .images(imageUrls)
                .leader(leader)
                .meetingDays(convertMeetingDays(request.getMeetingDays()))
                .build();

        // 생성된 Club 엔티티 저장
        clubRepository.save(club);

        ClubJoinList clubJoinList = ClubJoinList.builder()
                .club(club)
                .member(leader)
                .averageScore(leader.getMyaver())
                .clubRole(ClubRole.LEADER)  // 리더로 설정
                .status(RequestStatus.APPROVED)  // 가입 승인 상태로 설정
                .build();

        clubJoinListRepository.save(clubJoinList);
    }

    @Transactional
    public void updateClubSettings(Long clubId, String memberEmail, ClubUpdateDto updateDto, List<MultipartFile> images) throws IOException {
        Member leader = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));

        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("해당 클럽을 찾을 수 없습니다."));

        if (!isAuthorizedForClub(clubId, leader) && !leader.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("권한이 없습니다. 승인 작업은 해당 클럽의 LEADER 또는 MANAGER만 가능합니다.");
        }

        // 클럽 설정 업데이트
        clubRepository.updateClubSettings(
                clubId,
                updateDto.getName(),
                updateDto.getDescription(),
                updateDto.getLocation(),
                updateDto.getMaxMembers() != null ? updateDto.getMaxMembers() : club.getMaxMembers(),
                updateDto.getCategory(),
                updateDto.getRequirements(),
                updateDto.getMonthlyFee() != null ? updateDto.getMonthlyFee() : club.getMonthlyFee(),
                updateDto.getAverageScore() != null ? updateDto.getAverageScore() : club.getAverageScore()
        );

        if (updateDto.getMeetingDays() != null && !updateDto.getMeetingDays().isEmpty()) {
            clubRepository.updateClubMeetingDays(clubId, updateDto.getMeetingDays());
        }

        if (images != null && !images.isEmpty() && !images.get(0).isEmpty()) {
            List<String> imageUrls = s3ImageService.uploadMultiple(images.toArray(new MultipartFile[0]));
            clubRepository.updateClubImages(clubId, imageUrls);
        }
    }

    @Transactional
    public void updateRecruitmentStatus(Long clubId, String memberEmail, ClubRecruitmentUpdateDto recruitmentDto) {
        Member leader = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));

        clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("해당 클럽을 찾을 수 없습니다."));

        if (!isAuthorizedForClub(clubId, leader) && !leader.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("권한이 없습니다. 승인 작업은 해당 클럽의 LEADER 또는 MANAGER만 가능합니다.");
        }

        clubRepository.updateRecruitmentStatus(clubId, recruitmentDto.isRecruiting());
    }

    @Transactional
    public Page<ClubListResponseDto> getClubs(Pageable pageable) {
        return clubRepository.findAllByDeletedAtIsNull(pageable)
                .map(club -> ClubListResponseDto.builder()
                        .id(club.getId())
                        .name(club.getClubName())
                        .description(club.getDescription())
                        .location(club.getLocation())
                        .memberCount(club.getMembers().size())
                        .maxMembers(club.getMaxMembers())
                        .averageScore(club.getAverageScore())
                        .meetingDays(club.getMeetingDays())
                        .images(club.getImages())
                        .leader(new ClubMemberResponseDto(club.getLeader()))
                        .isRecruiting(club.isRecruiting())
                        .category(club.getCategory())
                        .requirements(club.getRequirements())
                        .monthlyFee(club.getMonthlyFee())
                        .establishedAt(club.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .build()
                );
    }

    @Transactional
    public ClubDetailResponseDto getClubDetail(Long id) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 클럽입니다."));

        return ClubDetailResponseDto.builder()
                .id(club.getId())
                .name(club.getClubName())
                .description(club.getDescription())
                .location(club.getLocation())
                .memberCount(club.getMembers().size())
                .maxMembers(club.getMaxMembers())
                .averageScore(club.getAverageScore())
                .meetingDays(club.getMeetingDays())
                .images(club.getImages())
                .leader(new ClubMemberResponseDto(club.getLeader()))
                .isRecruiting(club.isRecruiting())
                .category(club.getCategory())
                .requirements(club.getRequirements())
                .monthlyFee(club.getMonthlyFee())
                .establishedAt(club.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .build();
    }

    @Transactional
    public List<ClubMemberListResponseDto> getClubMembers(Long clubId) {
        List<ClubJoinList> clubJoinLists = clubJoinListRepository.findByClub_Id(clubId);

        if (clubJoinLists.isEmpty()) {
            throw new IllegalArgumentException("해당 클럽에 멤버가 없습니다.");
        }

        return clubJoinLists.stream().map(clubJoinList -> ClubMemberListResponseDto.builder()
                .members(new ClubMemberResponseDto(clubJoinList.getMember()))
                .role(clubJoinList.getClubRole())
                .joinedAt(clubJoinList.getClubJoinedAt())
                .averageScore(clubJoinList.getAverageScore())
                .status(clubJoinList.getStatus())
                .build()).collect(Collectors.toList());
    }

    @Transactional
    public ClubMemberDetailResponseDto getClubMemberDetail(Long clubId, Long userId, String leaderEmail) {
        // 리더 정보 확인 (리더 권한이 있는지 확인)
        Member leader = memberRepository.findByEmail(leaderEmail)
                .orElseThrow(() -> new SecurityException("유효하지 않은 사용자입니다."));

        if (!isAuthorizedForClub(clubId, leader) && !leader.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("권한이 없습니다. 승인 작업은 해당 클럽의 LEADER 또는 MANAGER만 가능합니다.");
        }

        // 조회 대상 회원의 상세 정보 조회
        ClubJoinList memberJoinList = clubJoinListRepository.findByClub_IdAndMember_Id(clubId, userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 클럽에 존재하지 않는 회원입니다."));

        // ClubMemberDetailResponseDto 객체로 반환
        return ClubMemberDetailResponseDto.builder()
                .members(new ClubMemberResponseDto(memberJoinList.getMember()))
                .role(memberJoinList.getClubRole())
                .joinedAt(memberJoinList.getClubJoinedAt())
                .averageScore(memberJoinList.getAverageScore())
                .status(memberJoinList.getStatus())
                .attendanceRate(memberJoinList.getAttendanceRate())
                .lastAttendance(memberJoinList.getLastAttendance())
                .recentScores(memberJoinList.getRecentScores())
                .build();
    }

    @Transactional
    public void updateMemberRole(Long clubId, Long userId, ClubMembersRoleUpdateDto request, String leaderEmail) {
        Member leader = memberRepository.findByEmail(leaderEmail)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));
        ClubJoinList leaderJoinList = clubJoinListRepository.findByClub_IdAndMember_Id(clubId, leader.getId())
                .orElseThrow(() -> new IllegalArgumentException("클럽에서 권한이 없는 사용자입니다."));

        if (!leaderJoinList.getClubRole().equals(ClubRole.LEADER) && !leader.getRole().equals(Role.ADMIN)) {
            throw new IllegalArgumentException("권한이 없는 사용자입니다.");
        }

        clubJoinListRepository.findByClub_IdAndMember_Id(clubId, userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 회원의 역할 업데이트
        clubJoinListRepository.updateClubRole(clubId, userId, request.getRole());
    }

    @Transactional
    public void updateMemberStatus(Long clubId, Long userId, ClubMemberStatusUpdateDto request, String leaderEmail) {
        Member leader = memberRepository.findByEmail(leaderEmail)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));
        ClubJoinList leaderJoinList = clubJoinListRepository.findByClub_IdAndMember_Id(clubId, leader.getId())
                .orElseThrow(() -> new IllegalArgumentException("클럽에서 권한이 없는 사용자입니다."));

        if (!leaderJoinList.getClubRole().equals(ClubRole.LEADER) && !leader.getRole().equals(Role.ADMIN)) {
            throw new IllegalArgumentException("권한이 없는 사용자입니다.");
        }

        clubJoinListRepository.findByClub_IdAndMember_Id(clubId, userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 상태가 INACTIVE일 때만 사유를 업데이트
        String reason = request.getStatus() == RequestStatus.INACTIVE ? request.getReason() : null;

        // 회원의 상태와 이유 업데이트
        clubJoinListRepository.updateClubStatus(clubId, userId, request.getStatus(), reason);
    }

    @Transactional
    public void removeMember(Long clubId, Long userId, String leaderEmail, String reason) {
        // 클럽과 사용자 유효성 검증
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 클럽입니다."));

        Member leader = memberRepository.findByEmail(leaderEmail)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));

        if (!isAuthorizedForClub(clubId, leader) && !leader.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("권한이 없습니다. 승인 작업은 해당 클럽의 LEADER 또는 MANAGER만 가능합니다.");
        }

        Member memberToRemove = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (!club.getMembers().contains(memberToRemove)) {
            throw new IllegalArgumentException("해당 회원은 클럽에 속해 있지 않습니다.");
        }

        // 클럽의 멤버 목록에서 제거
        club.getMembers().remove(memberToRemove);

        // 리포지토리에서 필드 업데이트
        clubJoinListRepository.updateClubStatus(clubId, userId, RequestStatus.INACTIVE, reason);
    }

    private boolean isAuthorizedForClub(Long clubId, Member member) {
        // 클럽 ID와 멤버 ID로 ClubJoinList를 조회
        ClubJoinList clubJoinList = clubJoinListRepository.findByClub_IdAndMember_Id(clubId, member.getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 클럽에 멤버가 존재하지 않습니다."));

        // ClubRole이 LEADER, MANAGER 역할인 경우 true 반환
        return (clubJoinList.getClubRole() == ClubRole.LEADER ||
                clubJoinList.getClubRole() == ClubRole.MANAGER);
    }

    @Transactional
    public List<String> convertMeetingDays(List<Boolean> meetingDays) {
        List<String> dayNames = Arrays.asList("월", "화", "수", "목", "금", "토", "일");
        List<String> result = new ArrayList<>();
        for (int i = 0; i < meetingDays.size(); i++) {
            if (Boolean.TRUE.equals(meetingDays.get(i))) {
                result.add(dayNames.get(i));
            }
        }
        return result;
    }
}
