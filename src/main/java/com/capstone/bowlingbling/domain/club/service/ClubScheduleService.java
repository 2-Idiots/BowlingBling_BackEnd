package com.capstone.bowlingbling.domain.club.service;

import com.capstone.bowlingbling.domain.club.domain.*;
import com.capstone.bowlingbling.domain.club.dto.clubSchedule.*;
import com.capstone.bowlingbling.domain.club.repository.*;
import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.domain.member.repository.MemberRepository;
import com.capstone.bowlingbling.global.enums.ClubRole;
import com.capstone.bowlingbling.global.enums.ParticipationStatus;
import com.capstone.bowlingbling.global.enums.Role;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClubScheduleService {

    private final ClubRepository clubRepository;
    private final ClubScheduleRepository scheduleRepository;
    private final ScheduleParticipantRepository participantRepository;
    private final MemberRepository memberRepository;
    private final ClubJoinListRepository clubJoinListRepository;

    @Transactional
    public Long createSchedule(Long clubId, String creatorEmail, ClubScheduleRequestDto request) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 클럽 ID 입니다."));
        Member creator = memberRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));

        ClubSchedule schedule = ClubSchedule.builder()
                .club(club)
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .maxParticipants(request.getMaxParticipants())
                .isRegular(request.getIsRegular())
                .deadlineDate(request.getDeadlineDate())
                .cancelableDate(request.getCancelableDate())
                .createdBy(creator)
                .frequency(request.getRepeatPattern().getFrequency())
                .daysOfWeek(request.getRepeatPattern().getDaysOfWeek())
                .repeatEndDate(request.getRepeatPattern().getRepeatEndDate())
                .build();

        return scheduleRepository.save(schedule).getId();
    }

    @Transactional
    public MonthlySchedulesResponseDto getMonthlySchedules(Long clubId, String year, String month) {
        List<ClubSchedule> schedules = scheduleRepository.findSchedulesByMonth(clubId, year, month);

        List<ClubScheduleListResponseDto> scheduleDtos = schedules.stream()
                .map(schedule -> ClubScheduleListResponseDto.builder()
                        .id(schedule.getId())
                        .title(schedule.getTitle())
                        .description(schedule.getDescription())
                        .location(schedule.getLocation())
                        .startDate(schedule.getStartDate())
                        .endDate(schedule.getEndDate())
                        .maxParticipants(schedule.getMaxParticipants())
                        .currentParticipants(schedule.getParticipants().size())
                        .isRegular(schedule.getIsRegular())
                        .deadlineDate(schedule.getDeadlineDate())
                        .cancelableDate(schedule.getCancelableDate())
                        .participants(schedule.getParticipants().stream().map(participant -> ParticipantDto.builder()
                                .userId(participant.getMember().getId())
                                .userName(participant.getMember().getName())
                                .userImage(participant.getMember().getImage())
                                .status(participant.getStatus())
                                .responseDate(participant.getResponseDate())
                                .comment(participant.getComment())
                                .build()).collect(Collectors.toList()))
                        .createdBy(CreatedByDto.builder()
                                .userId(schedule.getCreatedBy().getId())
                                .userName(schedule.getCreatedBy().getName())
                                .build())
                        .createdAt(schedule.getCreatedAt().toString())
                        .updatedAt(schedule.getModifiedAt().toString())
                        .build())
                .collect(Collectors.toList());

        return MonthlySchedulesResponseDto.builder()
                .schedules(scheduleDtos)
                .totalCount(scheduleDtos.size())
                .build();
    }

    @Transactional
    public void updateSchedule(Long clubId, Long scheduleId, ClubScheduleUpdateRequestDto request, String leaderEmail) {
        Member member = memberRepository.findByEmail(leaderEmail)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));

        ClubSchedule clubSchedule = scheduleRepository.findByIdAndClubId(scheduleId, clubId)
                .orElseThrow(() -> new IllegalArgumentException("해당 클럽 ID와 일정 ID에 해당하는 일정이 존재하지 않습니다."));

        if (!isAuthorizedForClub(clubId, member) && !member.getRole().equals(Role.ADMIN)) {
            throw new IllegalStateException("권한이 없습니다. 승인 작업은 해당 클럽의 LEADER 또는 MANAGER만 가능합니다.");
        }

        scheduleRepository.updateSchedule(
                scheduleId,
                request.getTitle(),
                request.getDescription(),
                request.getLocation(),
                request.getStartDate(),
                request.getEndDate(),
                request.getMaxParticipants() != null ? request.getMaxParticipants() : clubSchedule.getMaxParticipants(),
                request.getDeadlineDate(),
                request.getCancelableDate()
        );
    }

    @Transactional
    public void deleteSchedule(Long clubId, Long scheduleId, String leaderEmail) {
        Member member = memberRepository.findByEmail(leaderEmail)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));

        scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 일정이 존재하지 않습니다."));

        if (!isAuthorizedForClub(clubId, member) && !member.getRole().equals(Role.ADMIN)) {
            throw new IllegalStateException("권한이 없습니다. 승인 작업은 해당 클럽의 LEADER 또는 MANAGER만 가능합니다.");
        }
        // Repository에서 삭제 로직을 처리
        scheduleRepository.softDeleteSchedule(scheduleId, LocalDateTime.now());
    }

    @Transactional
    public void setParticipation(Long clubId, Long scheduleId, String memberEmail, ParticipationRequestDto request) {
        // 1. 해당 클럽 ID와 일정 ID에 해당하는 일정이 존재하는지 확인
        ClubSchedule schedule = scheduleRepository.findByIdAndClubId(scheduleId, clubId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 일정이나 클럽 ID입니다."));

        // 2. 세션 사용자(Member)를 이메일로 찾기
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));

        // 3. 기존에 해당 일정에 참여한 기록이 있는지 확인
        ScheduleParticipant existingParticipant = participantRepository.findByClubScheduleAndMember(schedule, member);

        // 4. 참여 기록이 없으면 새로 참여 추가
        if (existingParticipant == null) {
            participantRepository.save(
                    ScheduleParticipant.builder()
                            .clubSchedule(schedule)  // 해당 일정
                            .member(member)          // 해당 사용자
                            .status(request.getStatus())  // 참석 상태
                            .responseDate(LocalDateTime.now().toString())  // 응답 일시
                            .comment(request.getComment())  // 불참 사유 (선택 사항)
                            .build()
            );
        } else {
            // 기존 참여자가 있으면 상태만 업데이트
            participantRepository.updateParticipationStatus(
                    existingParticipant.getId(),
                    request.getStatus(),
                    request.getComment(),
                    LocalDateTime.now().toString()  // 응답 일시 업데이트
            );
        }
    }

    public ParticipantListDto getParticipants(Long clubId, Long scheduleId) {
        List<ScheduleParticipant> participants = participantRepository.findByClubScheduleIdAndStatus(scheduleId, ParticipationStatus.ATTENDING);

        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("해당 클럽을 찾을 수 없습니다."));

        List<ParticipantDto> participantDtos = participants.stream()
                .map(participant -> ParticipantDto.builder()
                        .userId(participant.getMember().getId())
                        .userName(participant.getMember().getName())
                        .userImage(participant.getMember().getImage())  // 이미지가 없다면 null일 수 있음
                        .status(participant.getStatus())
                        .responseDate(participant.getResponseDate())
                        .comment(participant.getComment())
                        .build())
                .collect(Collectors.toList());

        int attendingCount = participantRepository.countByStatus(scheduleId, ParticipationStatus.ATTENDING);
        int notAttendingCount = participantRepository.countByStatus(scheduleId, ParticipationStatus.NOT_ATTENDING);

        return ParticipantListDto.builder()
                .participant(participantDtos)       // 참가자 리스트
                .attending(attendingCount)       // 참석 수
                .notAttending(notAttendingCount) // 불참 수
                .number(club.getJoinList().size())     // 참가자 수
                .build();
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
