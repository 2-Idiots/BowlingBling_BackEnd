package com.capstone.bowlingbling.domain.club.service;

import com.capstone.bowlingbling.domain.club.domain.Club;
import com.capstone.bowlingbling.domain.club.domain.ClubJoinRequest;
import com.capstone.bowlingbling.domain.club.dto.request.ClubJoinRequestStatusDto;
import com.capstone.bowlingbling.domain.club.dto.request.ClubRequestDto;
import com.capstone.bowlingbling.domain.club.dto.response.ClubJoinResponseDto;
import com.capstone.bowlingbling.domain.club.dto.response.ClubResponseDto;
import com.capstone.bowlingbling.domain.club.dto.response.ClubResponseListDto;
import com.capstone.bowlingbling.domain.club.repository.ClubJoinRequestRepository;
import com.capstone.bowlingbling.domain.club.repository.ClubRepository;
import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.domain.member.repository.MemberRepository;
import com.capstone.bowlingbling.domain.place.domain.Place;
import com.capstone.bowlingbling.domain.place.dto.PlaceDto;
import com.capstone.bowlingbling.domain.place.repository.PlaceRepository;
import com.capstone.bowlingbling.global.enums.RequestStatus;
import com.capstone.bowlingbling.global.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClubService {

    private final ClubRepository clubRepository;
    private final ClubJoinRequestRepository clubJoinRequestRepository;
    private final MemberRepository memberRepository;
    private final PlaceRepository placeRepository;

    public Page<ClubResponseListDto> getAllClubs(Pageable pageable) {
        Page<Club> ClubPage = clubRepository.findAll(pageable);

        return ClubPage.map(club -> ClubResponseListDto.builder()
                .id(club.getId())
                .leaderNickname(club.getLeader().getNickname())
                .clubname(club.getClubname())
                .memberCount(club.getMemberCount())
                .build());
    }

    public ClubResponseDto getClubById(Long id) {
        return clubRepository.findById(id)
                .map(club -> ClubResponseDto.builder()
                        .id(club.getId())
                        .clubname(club.getClubname())
                        .introduction(club.getIntroduction())
                        .memberCount(club.getMemberCount())
                        .leaderNickname(club.getLeader().getNickname())
                        .build())
                .orElseThrow(() -> new IllegalArgumentException("해당하는 동호회가 조회되지 않습니다."));
    }

    public ClubResponseDto createClub(ClubRequestDto clubDto, String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자가 없습니다."));

        PlaceDto placeDto = clubDto.getPlace();
        Place place = placeRepository.findById(Long.valueOf(placeDto.getId()))
                .orElseGet(() -> placeRepository.save(Place.builder()
                        .id(Long.valueOf(placeDto.getId()))
                        .addressName(placeDto.getAddressName())
                        .roadAddressName(placeDto.getRoadAddressName())
                        .buildingName(placeDto.getBuildingName())
                        .zoneNo(placeDto.getZoneNo())
                        .latitude(placeDto.getY())
                        .longitude(placeDto.getX())
                        .placeName(placeDto.getPlaceName())
                        .build()));

        Club club = Club.builder()
                .clubname(clubDto.getClubname())
                .introduction(clubDto.getIntroduction())
                .leader(member)
                .memberCount(1)
                .place(place)
                .build();

        clubRepository.save(club);

        return ClubResponseDto.builder()
                .id(club.getId())
                .clubname(club.getClubname())
                .introduction(club.getIntroduction())
                .memberCount(club.getMemberCount())
                .leaderNickname(club.getLeader().getNickname())
                .placeName(place.getPlaceName())
                .build();
    }

    public void joinClub(Long clubId, String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자가 없습니다."));

        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 동호회가 조회되지 않습니다."));

        ClubJoinRequest joinRequest = ClubJoinRequest.builder()
                .club(club)
                .member(member)
                .status(RequestStatus.PENDING)
                .build();
        clubJoinRequestRepository.save(joinRequest);
    }

    public Page<ClubJoinResponseDto> getJoinRequests(Long clubId, String memberEmail, Pageable pageable) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자가 없습니다."));

        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("동호회를 찾을 수 없습니다."));

        if (!club.getLeader().equals(member) && !member.getRole().equals(Role.ADMIN)) {
            throw new IllegalArgumentException("인가되지 않은 권한입니다.");
        }

        Page<ClubJoinRequest> joinRequestPage = clubJoinRequestRepository.findAllByClubAndStatus(
                club, RequestStatus.PENDING, pageable
        );

        return joinRequestPage.map(joinRequest -> {
            Member m = joinRequest.getMember();
            return ClubJoinResponseDto.builder()
                    .id(m.getId())
                    .imageUrl(m.getImage())
                    .sex(m.getSex())
                    .age(m.getAge())
                    .phonenum(m.getPhonenum())
                    .build();
        });
    }

    public void acceptJoinRequest(Long clubId, Long memberId, ClubJoinRequestStatusDto statusDto, String memberEmail) {
        Member leader = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("동회화장을 찾을 수 없습니다."));

        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("동호회를 찾을 수 없습니다."));

        if (!club.getLeader().equals(leader) && !leader.getRole().equals(Role.ADMIN)) {
            throw new IllegalArgumentException("인가되지 않은 권한입니다.");
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        ClubJoinRequest joinRequest = clubJoinRequestRepository.findByClubAndMember(club, member)
                .orElseThrow(() -> new IllegalArgumentException("요청을 찾을 수 없습니다."));

        joinRequest.toBuilder()
                .status(statusDto.getStatus())
                .build();
        clubJoinRequestRepository.save(joinRequest);

        if (statusDto.getStatus() == RequestStatus.ACCEPTED) {
            member = member.toBuilder()
                    .club(club)
                    .build();

            List<Member> updatedMembers = new ArrayList<>(club.getMembers());
            updatedMembers.add(member);

            clubRepository.save(club.toBuilder()
                    .members(updatedMembers)
                    .memberCount(club.getMemberCount() + 1)
                    .build());
        }
    }

    public void updateClub(Long id, ClubRequestDto clubDto, String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자가 없습니다."));

        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("동호회를 찾을 수 없습니다."));

        if (!club.getLeader().equals(member) && !member.getRole().equals(Role.ADMIN)) {
            throw new IllegalArgumentException("인가되지 않은 권합입니다.");
        }

        clubRepository.updateClubInfo(id, clubDto.getClubname(), clubDto.getIntroduction());
    }

    public void deleteClub(Long id, String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자가 없습니다."));

        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("동호회를 찾을 수 없습니다."));

        if (!club.getLeader().equals(member) && !member.getRole().equals(Role.ADMIN)) {
            throw new IllegalArgumentException("인가되지 않은 권한입니다.");
        }

        clubRepository.delete(club);
    }
}
