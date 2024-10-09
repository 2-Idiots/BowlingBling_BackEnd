package com.capstone.bowlingbling.domain.gathering.service;

import com.capstone.bowlingbling.domain.gathering.domain.Gathering;
import com.capstone.bowlingbling.domain.gathering.domain.MemberGathering;
import com.capstone.bowlingbling.domain.gathering.dto.request.GatheringRequestDto;
import com.capstone.bowlingbling.domain.gathering.dto.response.GatheringDetailResponseDto;
import com.capstone.bowlingbling.domain.gathering.repository.GatheringRepository;
import com.capstone.bowlingbling.domain.image.service.S3ImageService;
import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.domain.member.repository.MemberRepository;
import com.capstone.bowlingbling.domain.place.repository.PlaceRepository;
import com.capstone.bowlingbling.global.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GatheringService {
    private final GatheringRepository gatheringRepository;
    private final MemberRepository memberRepository;
    private final PlaceRepository placeRepository;
    private final S3ImageService s3ImageService;

    @Transactional
    public void createGathering(GatheringRequestDto gatheringRequestDto, String memberEmail, List<MultipartFile> files) throws IOException {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException(memberEmail + " 멤버를 찾을 수 없습니다."));

//        PlaceDto placeDto = gatheringRequestDto.getPlace();
//        Place place = placeRepository.findById(Long.valueOf(placeDto.getId()))
//                .orElseGet(() -> placeRepository.save(Place.builder()
//                        .id(Long.valueOf(placeDto.getId()))
//                        .addressName(placeDto.getAddressName())
//                        .roadAddressName(placeDto.getRoadAddressName())
//                        .buildingName(placeDto.getBuildingName())
//                        .zoneNo(placeDto.getZoneNo())
//                        .latitude(placeDto.getY())
//                        .longitude(placeDto.getX())
//                        .placeName(placeDto.getPlaceName())
//                        .build()));

        List<String> imageUrls = s3ImageService.uploadMultiple(files.toArray(new MultipartFile[0]));

        Gathering gathering = Gathering.builder()
                .leader(member)
                .title(gatheringRequestDto.getTitle())
                .minAverage(gatheringRequestDto.getMinAverage())
                .maxAverage(gatheringRequestDto.getMaxAverage())
                .description(gatheringRequestDto.getDescription())
                .location(gatheringRequestDto.getLocation())
                .date(gatheringRequestDto.getDate())
                .maxParticipants(gatheringRequestDto.getMaxParticipants())
                .lat(gatheringRequestDto.getLat())
                .lng(gatheringRequestDto.getLng())
                .images(imageUrls)
//                .place(place)
                .build();

        gatheringRepository.save(gathering);
    }

    @Transactional(readOnly = true)
    public GatheringDetailResponseDto getGathering(Long id) {
        Gathering gathering = gatheringRepository.findActiveById(id);
        if (gathering == null) {
            throw new IllegalArgumentException(id + " 게시물을 찾을 수 없습니다.");
        }

        return GatheringDetailResponseDto.builder()
                .id(gathering.getId())
                .title(gathering.getTitle())
                .leadername(gathering.getLeader().getName())
                .minAverage(gathering.getMinAverage())
                .maxAverage(gathering.getMaxAverage())
                .description(gathering.getDescription())
                .location(gathering.getLocation())
                .lat(gathering.getLat())
                .lng(gathering.getLng())
                .date(gathering.getDate())
                .maxParticipants(gathering.getMaxParticipants())
                .currentParticipants(gathering.getCurrentParticipants())
                .images(gathering.getImages())
                .build();
    }

    @Transactional(readOnly = true)
    public Page<GatheringDetailResponseDto> getAllGatherings(Pageable pageable) {
        return gatheringRepository.findAllActive(pageable).map(gathering -> GatheringDetailResponseDto.builder()
                .id(gathering.getId())
                .title(gathering.getTitle())
                .minAverage(gathering.getMinAverage())
                .maxAverage(gathering.getMaxAverage())
                .description(gathering.getDescription())
                .location(gathering.getLocation())
                .date(gathering.getDate())
                .maxParticipants(gathering.getMaxParticipants())
                .currentParticipants(gathering.getCurrentParticipants())
                .images(gathering.getImages())
                .build());
    }

    @Transactional
    public GatheringDetailResponseDto updateGathering(Long id, GatheringDetailResponseDto gatheringRequestDto, String memberEmail) {
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
                .title(gatheringRequestDto.getTitle() != null ? gatheringRequestDto.getTitle() : gathering.getTitle())
                .minAverage(gatheringRequestDto.getMinAverage() != null ? gatheringRequestDto.getMinAverage() : gathering.getMinAverage())
                .maxAverage(gatheringRequestDto.getMaxAverage() != null ? gatheringRequestDto.getMaxAverage() : gathering.getMaxAverage())
                .description(gatheringRequestDto.getDescription() != null ? gatheringRequestDto.getDescription() : gathering.getDescription())
                .location(gatheringRequestDto.getLocation() != null ? gatheringRequestDto.getLocation() : gathering.getLocation())
                .date(gatheringRequestDto.getDate() != null ? gatheringRequestDto.getDate() : gathering.getDate())
                .maxParticipants(gatheringRequestDto.getMaxParticipants() != null ? gatheringRequestDto.getMaxParticipants() : gathering.getMaxParticipants())
                .build();

        Gathering updatedGathering = gatheringRepository.save(gathering);

        return GatheringDetailResponseDto.builder()
                .id(updatedGathering.getId())
                .title(updatedGathering.getTitle())
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
    public Page<GatheringDetailResponseDto> getMemberGatherings(String memberEmail, Pageable pageable) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalStateException(memberEmail + "에 해당하는 멤버를 찾을 수 없습니다."));

        return gatheringRepository.findByMember(member.getId(), pageable).map(gathering -> GatheringDetailResponseDto.builder()
                .id(gathering.getId())
                .title(gathering.getTitle())
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
