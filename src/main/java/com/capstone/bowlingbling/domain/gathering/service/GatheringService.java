package com.capstone.bowlingbling.domain.gathering.service;

import com.capstone.bowlingbling.domain.gathering.domain.Gathering;
import com.capstone.bowlingbling.domain.gathering.domain.MemberGathering;
import com.capstone.bowlingbling.domain.gathering.dto.request.GatheringRequestDto;
import com.capstone.bowlingbling.domain.gathering.dto.response.GatheringCreateResponseDto;
import com.capstone.bowlingbling.domain.gathering.repository.GatheringRepository;
import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.domain.member.repository.MemberRepository;
import com.capstone.bowlingbling.domain.place.domain.Place;
import com.capstone.bowlingbling.domain.place.dto.PlaceDto;
import com.capstone.bowlingbling.domain.place.repository.PlaceRepository;
import com.capstone.bowlingbling.global.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class GatheringService {
    private final GatheringRepository gatheringRepository;
    private final MemberRepository memberRepository;
    private final PlaceRepository placeRepository;

    @Transactional
    public GatheringCreateResponseDto createGathering(GatheringRequestDto gatheringRequestDto, String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException(memberEmail + " 멤버를 찾을 수 없습니다."));

        PlaceDto placeDto = gatheringRequestDto.getPlace();
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

        Gathering gathering = Gathering.builder()
                .leader(member)
                .name(gatheringRequestDto.getName())
                .minAverage(gatheringRequestDto.getMinAverage())
                .maxAverage(gatheringRequestDto.getMaxAverage())
                .description(gatheringRequestDto.getDescription())
                .location(gatheringRequestDto.getLocation())
                .date(gatheringRequestDto.getDate())
                .maxParticipants(gatheringRequestDto.getMaxParticipants())
                .place(place)
                .build();

        Gathering savedGathering = gatheringRepository.save(gathering);

        return GatheringCreateResponseDto.builder()
                .id(savedGathering.getId())
                .leadername(member.getNickname())
                .name(savedGathering.getName())
                .minAverage(savedGathering.getMinAverage())
                .maxAverage(savedGathering.getMaxAverage())
                .description(savedGathering.getDescription())
                .location(savedGathering.getLocation())
                .date(savedGathering.getDate())
                .maxParticipants(savedGathering.getMaxParticipants())
                .currentParticipants(savedGathering.getCurrentParticipants())
                .build();
    }

    @Transactional(readOnly = true)
    public GatheringRequestDto getGathering(Long id) {
        Gathering gathering = gatheringRepository.findActiveById(id);
        if (gathering == null) {
            throw new IllegalArgumentException(id + " 게시물을 찾을 수 없습니다.");
        }

        return GatheringRequestDto.builder()
                .id(gathering.getId())
                .name(gathering.getName())
                .minAverage(gathering.getMinAverage())
                .maxAverage(gathering.getMaxAverage())
                .description(gathering.getDescription())
                .location(gathering.getLocation())
                .date(gathering.getDate())
                .maxParticipants(gathering.getMaxParticipants())
                .currentParticipants(gathering.getCurrentParticipants())
                .build();
    }

    @Transactional(readOnly = true)
    public Page<GatheringRequestDto> getAllGatherings(Pageable pageable) {
        return gatheringRepository.findAllActive(pageable).map(gathering -> GatheringRequestDto.builder()
                .id(gathering.getId())
                .name(gathering.getName())
                .minAverage(gathering.getMinAverage())
                .maxAverage(gathering.getMaxAverage())
                .description(gathering.getDescription())
                .location(gathering.getLocation())
                .date(gathering.getDate())
                .maxParticipants(gathering.getMaxParticipants())
                .currentParticipants(gathering.getCurrentParticipants())
                .build());
    }

    @Transactional
    public GatheringRequestDto updateGathering(Long id, GatheringRequestDto gatheringRequestDto, String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException(memberEmail + " 멤버를 찾을 수 없습니다."));

        Gathering gathering = gatheringRepository.findActiveById(id);
        if (gathering == null) {
            throw new IllegalArgumentException(id + " 게시물을 찾을 수 없습니다.");
        }

        if (!gathering.getLeader().equals(member) && !member.getRole().equals(Role.ADMIN)) {
            throw new IllegalArgumentException("인가되지 않은 권한입니다.");
        }

        gathering = gathering.toBuilder()
                .name(gatheringRequestDto.getName() != null ? gatheringRequestDto.getName() : gathering.getName())
                .minAverage(gatheringRequestDto.getMinAverage() != null ? gatheringRequestDto.getMinAverage() : gathering.getMinAverage())
                .maxAverage(gatheringRequestDto.getMaxAverage() != null ? gatheringRequestDto.getMaxAverage() : gathering.getMaxAverage())
                .description(gatheringRequestDto.getDescription() != null ? gatheringRequestDto.getDescription() : gathering.getDescription())
                .location(gatheringRequestDto.getLocation() != null ? gatheringRequestDto.getLocation() : gathering.getLocation())
                .date(gatheringRequestDto.getDate() != null ? gatheringRequestDto.getDate() : gathering.getDate())
                .maxParticipants(gatheringRequestDto.getMaxParticipants() != null ? gatheringRequestDto.getMaxParticipants() : gathering.getMaxParticipants())
                .build();

        Gathering updatedGathering = gatheringRepository.save(gathering);

        return GatheringRequestDto.builder()
                .id(updatedGathering.getId())
                .name(updatedGathering.getName())
                .minAverage(updatedGathering.getMinAverage())
                .maxAverage(updatedGathering.getMaxAverage())
                .description(updatedGathering.getDescription())
                .location(updatedGathering.getLocation())
                .date(updatedGathering.getDate())
                .maxParticipants(updatedGathering.getMaxParticipants())
                .currentParticipants(updatedGathering.getCurrentParticipants())
                .build();
    }

    @Transactional
    public void deleteGathering(Long id, String memberEmail) {
        Gathering gathering = gatheringRepository.findActiveById(id);
        if (gathering == null) {
            throw new IllegalArgumentException(id + " 게시물을 찾을 수 없습니다.");
        }
        gathering.markAsDeleted();
        gatheringRepository.save(gathering);
    }

    @Transactional
    public void joinGathering(Long gatheringId, String memberEmail) {
        Gathering gathering = gatheringRepository.findActiveById(gatheringId);
        if (gathering == null) {
            throw new IllegalArgumentException(gatheringId + " 게시물을 찾을 수 없습니다.");
        }

        if (gathering.getCurrentParticipants() >= gathering.getMaxParticipants()) {
            throw new IllegalStateException("정원이 가득찼습니다.");
        }

        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalStateException(memberEmail + "에 해당하는 멤버를 찾을 수 없습니다."));

        gathering.getMemberGatherings().add(MemberGathering.builder()
                .gathering(gathering)
                .member(member)
                .build());

        gatheringRepository.save(gathering);
    }

    @Transactional(readOnly = true)
    public Page<GatheringRequestDto> getMemberGatherings(String memberEmail, Pageable pageable) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalStateException(memberEmail + "에 해당하는 멤버를 찾을 수 없습니다."));

        return gatheringRepository.findByMember(member.getId(), pageable).map(gathering -> GatheringRequestDto.builder()
                .id(gathering.getId())
                .name(gathering.getName())
                .minAverage(gathering.getMinAverage())
                .maxAverage(gathering.getMaxAverage())
                .description(gathering.getDescription())
                .location(gathering.getLocation())
                .date(gathering.getDate())
                .maxParticipants(gathering.getMaxParticipants())
                .currentParticipants(gathering.getCurrentParticipants())
                .build());
    }
}
