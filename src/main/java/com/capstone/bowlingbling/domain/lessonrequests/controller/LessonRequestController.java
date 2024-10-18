package com.capstone.bowlingbling.domain.lessonrequests.controller;

import com.capstone.bowlingbling.domain.lessonrequests.dto.LessonRequestCreateDto;
import com.capstone.bowlingbling.domain.lessonrequests.dto.LessonRequestMyTeachersDto;
import com.capstone.bowlingbling.domain.lessonrequests.dto.LessonRequestStatusDto;
import com.capstone.bowlingbling.domain.lessonrequests.dto.LessonRequestStudentListDto;
import com.capstone.bowlingbling.domain.lessonrequests.service.LessonRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lesson-request")
@RequiredArgsConstructor
@Tag(name = "Lesson Request", description = "레슨 요청 관리 API")
public class LessonRequestController {

    private final LessonRequestService lessonRequestService;

    @PostMapping
    @Operation(summary = "레슨 요청 생성", description = "학생이 레슨 요청을 생성하는 API입니다.")
    public ResponseEntity<String> createLessonRequest(
            @AuthenticationPrincipal User sessionStudent,
            @RequestBody LessonRequestCreateDto request) {

        String studentEmail = sessionStudent.getUsername();
        String result = lessonRequestService.createLessonRequest(request, studentEmail);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/accept")
    @Operation(summary = "레슨 요청 상태 업데이트", description = "선생님이 레슨 요청을 수락 또는 거절하는 API입니다.")
    public ResponseEntity<String> updateLessonRequestStatus(
            @AuthenticationPrincipal User sessionTeacher,
            @RequestBody LessonRequestStatusDto request) {

        String teacherEmail = sessionTeacher.getUsername();
        String result = lessonRequestService.updateLessonRequestStatus(request, teacherEmail);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/my-teachers")
    @Operation(summary = "내 레슨 요청 조회", description = "학생이 자신이 요청한 레슨 목록을 조회하는 API입니다.")
    public ResponseEntity<List<LessonRequestMyTeachersDto>> getMyLessonRequests(
            @AuthenticationPrincipal User sessionStudent) {

        String studentEmail = sessionStudent.getUsername();
        List<LessonRequestMyTeachersDto> myLessonRequests = lessonRequestService.getMyLessonRequests(studentEmail);

        return ResponseEntity.ok(myLessonRequests);
    }

    @GetMapping("/received-requests")
    @Operation(summary = "받은 레슨 요청 조회", description = "선생님이 자신에게 온 레슨 요청 목록을 조회하는 API입니다.")
    public ResponseEntity<List<LessonRequestStudentListDto>> getReceivedRequests(
            @AuthenticationPrincipal User sessionTeacher) {

        String teacherEmail = sessionTeacher.getUsername();
        List<LessonRequestStudentListDto> requests = lessonRequestService.getReceivedRequests(teacherEmail);

        return ResponseEntity.ok(requests);
    }
}