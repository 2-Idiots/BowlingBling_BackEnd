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

        if (!isAuthorizedForClub(clubId, leader)) {
            throw new IllegalStateException("권한이 없습니다. 가입 조회 작업은 해당 클럽의 LEADER 또는 MANAGER만 가능합니다.");
        }

        Page<ClubJoinList> joinRequests = clubJoinListRepository.findByClubIdAndStatus(clubId, RequestStatus.PENDING, pageable);

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

        ClubJoinList joinRequest = clubJoinListRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("가입 신청을 찾을 수 없습니다."));

        if (!isAuthorizedForClub(clubId, leader)) {
            throw new IllegalStateException("권한이 없습니다. 승인 작업은 해당 클럽의 LEADER 또는 MANAGER만 가능합니다.");
        }

        clubJoinListRepository.updateJoinRequestStatus(requestId, RequestStatus.APPROVED);

        Club club = joinRequest.getClub();
        Member member = joinRequest.getMember();
        club.getMembers().add(member);

        // 변경 사항 저장
        clubRepository.save(club);
    }

    @Transactional
    public void rejectJoinRequest(Long clubId, Long requestId, String leaderEmail) {
        Member leader = memberRepository.findByEmail(leaderEmail)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));
        clubJoinListRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("가입 신청을 찾을 수 없습니다."));

        if (!isAuthorizedForClub(clubId, leader)) {
            throw new IllegalStateException("권한이 없습니다. 승인 작업은 해당 클럽의 LEADER 또는 MANAGER만 가능합니다.");
        }

        clubJoinListRepository.updateJoinRequestStatus(requestId, RequestStatus.REJECTED);
    }

    private boolean isAuthorizedForClub(Long clubId, Member member) {
        return member.getClub().getId().equals(clubId) &&
                (member.getClubRole() == ClubRole.LEADER || member.getClubRole() == ClubRole.MANAGER || member.getRole().equals(Role.ADMIN));
    }
}
