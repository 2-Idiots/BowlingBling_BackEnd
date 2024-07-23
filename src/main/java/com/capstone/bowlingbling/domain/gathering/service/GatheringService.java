package com.capstone.bowlingbling.domain.gathering.service;

import com.capstone.bowlingbling.domain.gathering.domain.Gathering;
import com.capstone.bowlingbling.domain.gathering.domain.MemberGathering;
import com.capstone.bowlingbling.domain.gathering.dto.GatheringDto;
import com.capstone.bowlingbling.domain.gathering.repository.GatheringRepository;
import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GatheringService {
    private final GatheringRepository gatheringRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public GatheringDto createGathering(GatheringDto gatheringDto, String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException(memberEmail + " 멤버를 찾을 수 없습니다."));

        Gathering gathering = Gathering.builder()
                .name(gatheringDto.getName())
                .minAverage(gatheringDto.getMinAverage())
                .maxAverage(gatheringDto.getMaxAverage())
                .description(gatheringDto.getDescription())
                .location(gatheringDto.getLocation())
                .date(gatheringDto.getDate())
                .maxParticipants(gatheringDto.getMaxParticipants())
                .build();

        Gathering savedGathering = gatheringRepository.save(gathering);

        return GatheringDto.builder()
                .id(savedGathering.getId())
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
    public GatheringDto getGathering(Long id) {
        Gathering gathering = gatheringRepository.findActiveById(id);
        if (gathering == null) {
            throw new IllegalArgumentException(id + " 게시물을 찾을 수 없습니다.");
        }

        return GatheringDto.builder()
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
    public Page<GatheringDto> getAllGatherings(Pageable pageable) {
        return gatheringRepository.findAllActive(pageable).map(gathering -> GatheringDto.builder()
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
    public GatheringDto updateGathering(Long id, GatheringDto gatheringDto, String memberEmail) {
        Gathering gathering = gatheringRepository.findActiveById(id);
        if (gathering == null) {
            throw new IllegalArgumentException(id + " 게시물을 찾을 수 없습니다.");
        }

        gathering = gathering.toBuilder()
                .name(gatheringDto.getName() != null ? gatheringDto.getName() : gathering.getName())
                .minAverage(gatheringDto.getMinAverage() != null ? gatheringDto.getMinAverage() : gathering.getMinAverage())
                .maxAverage(gatheringDto.getMaxAverage() != null ? gatheringDto.getMaxAverage() : gathering.getMaxAverage())
                .description(gatheringDto.getDescription() != null ? gatheringDto.getDescription() : gathering.getDescription())
                .location(gatheringDto.getLocation() != null ? gatheringDto.getLocation() : gathering.getLocation())
                .date(gatheringDto.getDate() != null ? gatheringDto.getDate() : gathering.getDate())
                .maxParticipants(gatheringDto.getMaxParticipants() != null ? gatheringDto.getMaxParticipants() : gathering.getMaxParticipants())
                .build();

        Gathering updatedGathering = gatheringRepository.save(gathering);

        return GatheringDto.builder()
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
            throw new IllegalStateException("This gathering is already full.");
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
    public Page<GatheringDto> getMemberGatherings(String memberEmail, Pageable pageable) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalStateException(memberEmail + "에 해당하는 멤버를 찾을 수 없습니다."));

        return gatheringRepository.findByMember(member.getId(), pageable).map(gathering -> GatheringDto.builder()
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
