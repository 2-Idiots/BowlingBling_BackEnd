package com.capstone.bowlingbling.domain.club.service;

import com.capstone.bowlingbling.domain.club.domain.Club;
import com.capstone.bowlingbling.domain.club.domain.ClubJoinList;
import com.capstone.bowlingbling.domain.club.dto.request.ClubJoinRequestDto;
import com.capstone.bowlingbling.domain.club.dto.response.ClubJoinListResponseDto;
import com.capstone.bowlingbling.domain.club.dto.response.ClubMemberResponseDto;
import com.capstone.bowlingbling.domain.club.repository.ClubJoinListRepository;
import com.capstone.bowlingbling.domain.club.repository.ClubRepository;
import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.domain.member.repository.MemberRepository;
import com.capstone.bowlingbling.global.enums.ClubRole;
import com.capstone.bowlingbling.global.enums.RequestStatus;
import com.capstone.bowlingbling.global.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClubJoinListService {

    private final ClubRepository clubRepository;
    private final MemberRepository memberRepository;
    private final ClubJoinListRepository clubJoinListRepository;


    @Transactional
    public Long createJoinRequest(Long clubId, String memberEmail, ClubJoinRequestDto request) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("클럽을 찾을 수 없습니다."));
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));

        ClubJoinList joinRequest = ClubJoinList.builder()
                .club(club)
                .member(member)
                .averageScore(request.getAverageScore())
                .experience(request.getExperience())
                .motivation(request.getMotivation())
                .availability(request.getAvailability())
                .status(RequestStatus.PENDING)
                .build();

        clubJoinListRepository.save(joinRequest);
        return joinRequest.getId();
    }

    @Transactional(readOnly = true)
    public Page<ClubJoinListResponseDto> getJoinRequests(Long clubId, Pageable pageable, String leaderEmail) {
        Member leader = memberRepository.findByEmail(leaderEmail)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));

        if (!isAuthorizedForClub(clubId, leader) && !leader.getRole().equals(Role.ADMIN)) {
            throw new IllegalStateException("권한이 없습니다. 가입 조회 작업은 해당 클럽의 LEADER 또는 MANAGER만 가능합니다.");
        }

        List<RequestStatus> statuses = Arrays.asList(RequestStatus.PENDING, RequestStatus.ACTIVE);
        Page<ClubJoinList> joinRequests = clubJoinListRepository.findByClubIdAndStatuses(clubId, statuses, pageable);

        return joinRequests.map(request -> ClubJoinListResponseDto.builder()
                .id(request.getId())
                .userId(request.getMember().getId())
                .clubId(request.getClub().getId())
                .averageScore(request.getAverageScore())
                .experience(request.getExperience())
                .motivation(request.getMotivation())
                .availability(request.getAvailability())
                .status(request.getStatus())
                .createdAt(request.getCreatedAt())
                .user(new ClubMemberResponseDto(request.getMember()))
                .build());
    }

    @Transactional
    public void approveJoinRequest(Long clubId, Long requestId, String leaderEmail) {
        Member leader = memberRepository.findByEmail(leaderEmail)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));
        ClubJoinList clubJoinList = clubJoinListRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("가입 신청을 찾을 수 없습니다."));

        if (!isAuthorizedForClub(clubId, leader) && !leader.getRole().equals(Role.ADMIN)) {
            throw new IllegalStateException("권한이 없습니다. 승인 작업은 해당 클럽의 LEADER 또는 MANAGER만 가능합니다.");
        }

        Club club = clubJoinList.getClub();
        Member member = clubJoinList.getMember();
        if (!club.getMembers().contains(member)) {
            club.getMembers().add(member);
        }
        clubJoinListRepository.updateJoinRequestStatus(requestId, RequestStatus.ACTIVE);
    }

    @Transactional
    public void rejectJoinRequest(Long clubId, Long requestId, String leaderEmail) {
        Member leader = memberRepository.findByEmail(leaderEmail)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));
        clubJoinListRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("가입 신청을 찾을 수 없습니다."));

        if (!isAuthorizedForClub(clubId, leader) && !leader.getRole().equals(Role.ADMIN)) {
            throw new IllegalStateException("권한이 없습니다. 승인 작업은 해당 클럽의 LEADER 또는 MANAGER만 가능합니다.");
        }

        clubJoinListRepository.updateJoinRequestStatus(requestId, RequestStatus.REJECTED);
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
