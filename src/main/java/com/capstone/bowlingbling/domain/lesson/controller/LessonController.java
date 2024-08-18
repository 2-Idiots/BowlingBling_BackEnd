package com.capstone.bowlingbling.domain.lesson.controller;

import com.capstone.bowlingbling.domain.lesson.dto.request.LessonNoteDto;
import com.capstone.bowlingbling.domain.lesson.dto.request.LessonRequestDto;
import com.capstone.bowlingbling.domain.lesson.dto.response.LessonResponseDto;
import com.capstone.bowlingbling.domain.lesson.service.LessonService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lessons")
public class LessonController {

    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @Operation(summary = "레슨 요청", description = "선생님에게 레슨을 요청합니다.")
    @PostMapping("/request")
    public ResponseEntity<LessonResponseDto> requestLesson(@RequestBody LessonRequestDto requestDto,
                                                           @AuthenticationPrincipal User sessionMember) {
        String studentEmail = sessionMember.getUsername();
        LessonResponseDto responseDto = lessonService.requestLesson(requestDto, studentEmail);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "대기 중인 레슨 요청 조회", description = "선생님이 대기 중인 레슨 요청을 조회합니다.")
    @GetMapping("/requests/pending")
    public ResponseEntity<List<LessonResponseDto>> getPendingLessonRequests(@AuthenticationPrincipal User sessionMember) {
        String teacherEmail = sessionMember.getUsername();
        List<LessonResponseDto> pendingRequests = lessonService.getPendingLessonRequests(teacherEmail);
        return ResponseEntity.ok(pendingRequests);
    }

    @Operation(summary = "레슨 요청 응답", description = "선생님이 레슨 요청에 응답합니다.")
    @PostMapping("/requests/{requestId}/respond")
    public ResponseEntity<Void> respondToLessonRequest(@PathVariable Long requestId,
                                                       @RequestParam boolean isAccepted,
                                                       @AuthenticationPrincipal User sessionMember) {
        String teacherEmail = sessionMember.getUsername();
        lessonService.respondToLessonRequest(requestId, isAccepted, teacherEmail);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "레슨 기록", description = "선생님이 학생의 레슨을 기록합니다.")
    @PostMapping("/record")
    public ResponseEntity<Void> recordLesson(@RequestBody LessonNoteDto lessonDto,
                                             @AuthenticationPrincipal User sessionMember) {
        String teacherEmail = sessionMember.getUsername();
        lessonService.recordLesson(lessonDto, teacherEmail);
        return ResponseEntity.ok().build();
    }
}
