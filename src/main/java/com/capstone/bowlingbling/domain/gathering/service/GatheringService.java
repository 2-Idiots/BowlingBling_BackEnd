package com.capstone.bowlingbling.domain.gathering.service;

import com.capstone.bowlingbling.domain.gathering.domain.Gathering;
import com.capstone.bowlingbling.domain.gathering.domain.MemberGathering;
import com.capstone.bowlingbling.domain.gathering.dto.GatheringDto;
import com.capstone.bowlingbling.domain.gathering.mapper.GatheringMapper;
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
    private final GatheringMapper gatheringMapper;

    @Transactional
    public GatheringDto createGathering(GatheringDto gatheringDto, String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException(memberEmail + "멤버를 찾을 수 없습니다."));

        Gathering gathering = gatheringMapper.toEntity(gatheringDto);

        Gathering savedGathering = gatheringRepository.save(gathering);
        return gatheringMapper.toDto(savedGathering);
    }

    @Transactional(readOnly = true)
    public GatheringDto getGathering(Long id) {
        Gathering gathering = gatheringRepository.findActiveById(id);
        if (gathering == null) {
            throw new IllegalArgumentException(id + "게시물을 찾을 수 없습니다.");
        }
        return gatheringMapper.toDto(gathering);
    }

    @Transactional(readOnly = true)
    public Page<GatheringDto> getAllGatherings(Pageable pageable) {
        return gatheringRepository.findAllActive(pageable).map(gatheringMapper::toDto);
    }

    @Transactional
    public GatheringDto updateGathering(Long id, GatheringDto gatheringDto, String memberEmail) {
        Gathering gathering = gatheringRepository.findActiveById(id);
        if (gathering == null) {
            throw new IllegalArgumentException(id + "게시물을 찾을 수 없습니다.");
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
        return gatheringMapper.toDto(updatedGathering);
    }

    @Transactional
    public void deleteGathering(Long id, String memberEmail) {
        Gathering gathering = gatheringRepository.findActiveById(id);
        if (gathering == null) {
            throw new IllegalArgumentException(id + "게시물을 찾을 수 없습니다.");
        }
        gathering.markAsDeleted();
        gatheringRepository.save(gathering);
    }

    @Transactional
    public void joinGathering(Long gatheringId, String memberEmail) {
        Gathering gathering = gatheringRepository.findActiveById(gatheringId);
        if (gathering == null) {
            throw new IllegalArgumentException(gatheringId + "게시물을 찾을 수 없습니다.");
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

        return gatheringRepository.findByMember(member.getId(), pageable).map(gatheringMapper::toDto);
    }
}
