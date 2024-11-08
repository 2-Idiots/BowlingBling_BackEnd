package com.capstone.bowlingbling.domain.club.controller;

import com.capstone.bowlingbling.domain.club.dto.clubSchedule.*;
import com.capstone.bowlingbling.domain.club.service.ClubScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;

@RestController
@RequestMapping("/clubs/{clubId}/schedules")
@RequiredArgsConstructor
public class ClubScheduleController {
    private final ClubScheduleService clubScheduleService;

    @PostMapping
    @Operation(summary = "일정 생성", description = "클럽의 일정을 생성합니다.")
    public ResponseEntity<String> createSchedule(
            @PathVariable Long clubId,
            @RequestBody ClubScheduleRequestDto request,
            @AuthenticationPrincipal User sessionMember) {
        Long scheduleId = clubScheduleService.createSchedule(clubId, sessionMember.getUsername(), request);
        return ResponseEntity.ok("ID : " + scheduleId + " 으로 스케줄이 생성되었습니다.");
    }

    @GetMapping("/monthly")
    @Operation(summary = "일정 조회 (월별)", description = "월별 일정 목록을 조회합니다.")
    public ResponseEntity<MonthlySchedulesResponseDto> getMonthlySchedules(
            @PathVariable Long clubId,
            @RequestParam String year,
            @RequestParam String month) {
        return ResponseEntity.ok(clubScheduleService.getMonthlySchedules(clubId, year, month));
    }

    @PatchMapping("/{scheduleId}")
    @Operation(summary = "일정 수정", description = "클럽의 일정을 수정합니다.")
    public ResponseEntity<Void> updateSchedule(
            @PathVariable Long clubId,
            @PathVariable Long scheduleId,
            @RequestBody ClubScheduleUpdateRequestDto request,
            @AuthenticationPrincipal User sessionMember) {
        clubScheduleService.updateSchedule(clubId, scheduleId, request, sessionMember.getUsername());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{scheduleId}")
    @Operation(summary = "일정 삭제", description = "클럽의 일정을 삭제합니다.")
    public ResponseEntity<Void> deleteSchedule(
            @PathVariable Long clubId,
            @PathVariable Long scheduleId,
            @AuthenticationPrincipal User sessionMember) {
        clubScheduleService.deleteSchedule(clubId, scheduleId, sessionMember.getUsername());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{scheduleId}/participation")
    @Operation(summary = "참석/불참 처리", description = "일정에 대한 참석 여부를 처리합니다.")
    public ResponseEntity<String> setParticipation(
            @PathVariable Long clubId,
            @PathVariable Long scheduleId,
            @RequestBody ParticipationRequestDto request,
            @AuthenticationPrincipal User sessionMember) {
        clubScheduleService.setParticipation(clubId, scheduleId, sessionMember.getUsername(), request);
        return ResponseEntity.ok("일정 참석이 반영되었습니다.");
    }

    @GetMapping("/{scheduleId}/participants")
    @Operation(summary = "참석자 목록 조회", description = "일정의 참석자 목록을 조회합니다.")
    public ResponseEntity<ParticipantListDto> getParticipants(
            @PathVariable Long clubId,
            @PathVariable Long scheduleId) {
        return ResponseEntity.ok(clubScheduleService.getParticipants(clubId, scheduleId));
    }
}
