package com.capstone.bowlingbling.domain.club.service;

import com.capstone.bowlingbling.domain.club.domain.Club;
import com.capstone.bowlingbling.domain.club.domain.ClubJoinList;
import com.capstone.bowlingbling.domain.club.dto.request.ClubCreateDto;
import com.capstone.bowlingbling.domain.club.dto.request.ClubJoinRequestDto;
import com.capstone.bowlingbling.domain.club.dto.request.ClubMembersRoleUpdateDto;
import com.capstone.bowlingbling.domain.club.dto.response.ClubDetailResponseDto;
import com.capstone.bowlingbling.domain.club.dto.response.ClubMemberListResponseDto;
import com.capstone.bowlingbling.domain.club.dto.response.ClubMemberResponseDto;
import com.capstone.bowlingbling.domain.club.dto.response.ClubListResponseDto;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClubService {

    private final ClubRepository clubRepository;
    private final ClubJoinListRepository clubJoinListRepository;
    private final MemberRepository memberRepository;
    private final S3ImageService s3ImageService;

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
                .images(imageUrls)
                .leader(leader)
                .meetingDays(convertMeetingDays(request.getMeetingDays()))
                .build();

        // 생성된 Club 엔티티 저장
        clubRepository.save(club);
        memberRepository.updateMemberClubInfo(memberEmail, club, ClubRole.LEADER, LocalDate.now().toString());
    }

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

    public List<ClubMemberListResponseDto> getClubMembers(Long clubId) {
        List<Member> members = clubRepository.findMembersByClubId(clubId);

        if (members.isEmpty()) {
            throw new IllegalArgumentException("해당 클럽에 멤버가 없습니다.");
        }

        return members.stream().map(member -> ClubMemberListResponseDto.builder()
                .members(new ClubMemberResponseDto(member))
                .role(member.getClubRole())  // member 엔티티의 role 필드 사용
                .joinedAt(member.getClubJoinedAt())  // member 엔티티의 joinedAt 필드 사용 및 포맷팅
                .averageScore(member.getMyaver())  // averageScore도 member 엔티티에서 가져옴
                .build()).collect(Collectors.toList());
    }

    @Transactional
    public void updateMemberRole(Long clubId, Long userId, ClubMembersRoleUpdateDto request, String leaderEmail) {
        Member leader = memberRepository.findByEmail(leaderEmail)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));
        if (!leader.getClubRole().equals(ClubRole.LEADER) && !leader.getRole().equals(Role.ADMIN)) {
            throw new IllegalArgumentException("권한이 없는 사용자입니다.");
        }

        if (!memberRepository.existsById(userId)) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        // 회원의 역할 업데이트
        memberRepository.updateMemberRole(userId, clubId, request.getRole());
    }

    public void requestToJoinClub(Long clubId, String memberEmail) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 클럽 ID 입니다. " + clubId));
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 이메일입니다. " + memberEmail));

        ClubJoinList joinRequest = ClubJoinList.builder()
                .club(club)
                .member(List.of(member))
                .status(RequestStatus.PENDING)
                .build();

        clubJoinListRepository.save(joinRequest);
    }

    public String decideJoinRequest(Long clubId, ClubJoinRequestDto requestDto, String leaderEmail) {
        Member member = memberRepository.findByEmail(leaderEmail)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 이메일입니다. " + leaderEmail));

        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 클럽입니다."));

        // 요청한 사용자가 동호회장인지 확인하거나 ADMIN 권한을 갖고 있는지 확인
        if (!club.getLeader().getEmail().equals(leaderEmail) && !member.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("가입 요청을 결정할 권한이 없습니다.");
        }
        // ClubJoinList에 요청한 상태를 업데이트
        ClubJoinList joinList = clubJoinListRepository.findByClubAndMember(club, requestDto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("가입 요청이 존재하지 않습니다."));

        // 요청 상태를 ACCEPTED 또는 REJECTED로 업데이트
        clubJoinListRepository.save(joinList);

        return requestDto.getStatus().name();
    }

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
