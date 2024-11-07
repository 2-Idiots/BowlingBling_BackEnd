package com.capstone.bowlingbling.domain.gathering.service;

import com.capstone.bowlingbling.domain.gathering.domain.Gathering;
import com.capstone.bowlingbling.domain.gathering.domain.MemberGathering;
import com.capstone.bowlingbling.domain.gathering.dto.request.GatheringRequestDto;
import com.capstone.bowlingbling.domain.gathering.dto.request.GatheringUpdateRequestDto;
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
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GatheringService {
    private final GatheringRepository gatheringRepository;
    private final MemberRepository memberRepository;
    private final S3ImageService s3ImageService;

    @Transactional
    public void createGathering(GatheringRequestDto gatheringRequestDto, String memberEmail, List<MultipartFile> files) throws IOException {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException(memberEmail + " 멤버를 찾을 수 없습니다."));

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
                .build();

        gatheringRepository.save(gathering);
    }

    @Transactional(readOnly = true)
    public GatheringDetailResponseDto getGathering(Long id) {
        Gathering gathering = gatheringRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("번개모임을 찾을 수 없습니다. id : " + id));

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
                .leadername(gathering.getLeader().getName())
                .title(gathering.getTitle())
                .minAverage(gathering.getMinAverage())
                .maxAverage(gathering.getMaxAverage())
                .description(gathering.getDescription())
                .location(gathering.getLocation())
                .date(gathering.getDate())
                .lat(gathering.getLat())
                .lng(gathering.getLng())
                .maxParticipants(gathering.getMaxParticipants())
                .currentParticipants(gathering.getCurrentParticipants())
                .images(gathering.getImages())
                .build());
    }

    @Transactional
    public void updateGathering(Long id, GatheringUpdateRequestDto gatheringRequestDto, String memberEmail, List<MultipartFile> newImages) throws IOException {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException(memberEmail + " 멤버를 찾을 수 없습니다."));

        Gathering gathering = gatheringRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("번개모임을 찾을 수 없습니다. id : " + id));

        if (!gathering.getLeader().equals(member) && !member.getRole().equals(Role.ADMIN)) {
            throw new IllegalArgumentException("인가되지 않은 권한입니다.");
        }

        gatheringRepository.updateGathering(
                id,
                gatheringRequestDto.getTitle(),
                gatheringRequestDto.getMinAverage() != null ? gatheringRequestDto.getMinAverage() : gathering.getMinAverage(),
                gatheringRequestDto.getMaxAverage() != null ? gatheringRequestDto.getMaxAverage() : gathering.getMaxAverage(),
                gatheringRequestDto.getDescription(),
                gatheringRequestDto.getLocation(),
                LocalDate.from(gatheringRequestDto.getDate() != null ? gatheringRequestDto.getDate() : gathering.getDate()),
                gatheringRequestDto.getMaxParticipants() != null ? gatheringRequestDto.getMaxParticipants() : gathering.getMaxParticipants(),
                gatheringRequestDto.getLat(),
                gatheringRequestDto.getLng()
        );

        if (newImages != null && !newImages.isEmpty() && !newImages.get(0).isEmpty()) {
            List<String> imageUrls = s3ImageService.uploadMultiple(newImages.toArray(new MultipartFile[0]));
            gathering.getImages().clear();  // 기존 이미지 제거
            gathering.getImages().addAll(imageUrls);
        }

        gatheringRepository.save(gathering);
    }

    @Transactional
    public void deleteGathering(Long id, String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException(memberEmail + " 멤버를 찾을 수 없습니다."));

        Gathering gathering = gatheringRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("번개모임을 찾을 수 없습니다. id : " + id));

        if (!gathering.getLeader().equals(member) && !member.getRole().equals(Role.ADMIN)) {
            throw new IllegalArgumentException("인가되지 않은 권한입니다.");
        }

        gathering.markAsDeleted();
        gatheringRepository.save(gathering);
    }

    @Transactional
    public void joinGathering(Long gatheringId, String memberEmail) {
        Gathering gathering = gatheringRepository.findById(gatheringId)
                .orElseThrow(() -> new IllegalArgumentException("번개모임을 찾을 수 없습니다. id : " + gatheringId));

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
