package com.capstone.bowlingbling.domain.lessonbook.controller;

import com.capstone.bowlingbling.domain.lessonbook.dto.*;
import com.capstone.bowlingbling.domain.lessonbook.service.LessonBookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lesson-booking")
@RequiredArgsConstructor
@Tag(name = "Lesson Booking", description = "레슨 요청 관리 API")
public class LessonBookController {

    private final LessonBookService lessonBookService;

    @PostMapping
    @Operation(summary = "레슨 요청 생성", description = "학생이 레슨 요청을 생성하는 API입니다.")
    public ResponseEntity<String> createLessonRequest(
            @AuthenticationPrincipal User sessionStudent,
            @RequestBody LessonBookCreateDto request) {

        String studentEmail = sessionStudent.getUsername();
        String result = lessonBookService.createLessonRequest(request, studentEmail);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{lessonInfoId}/dates")
    @Operation(summary = "레슨 요청 조회", description = "모든 레슨 요청을 조회하는 APi입니다.")
    public ResponseEntity<List<LessonBookDateTimeDto>> getLessonDatesAndTimes(@PathVariable Long lessonInfoId) {
        List<LessonBookDateTimeDto> dateTimeList = lessonBookService.getLessonDatesAndTimes(lessonInfoId);
        return ResponseEntity.ok(dateTimeList);
    }

    @PatchMapping("/accept")
    @Operation(summary = "레슨 요청 상태 업데이트", description = "선생님이 레슨 요청을 수락 또는 거절하는 API입니다.")
    public ResponseEntity<String> updateLessonRequestStatus(
            @AuthenticationPrincipal User sessionTeacher,
            @RequestBody LessonBookStatusDto request) {

        String teacherEmail = sessionTeacher.getUsername();
        String result = lessonBookService.updateLessonRequestStatus(request, teacherEmail);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/my-teachers")
    @Operation(summary = "내 레슨 요청 조회", description = "학생이 자신이 요청한 레슨 목록을 조회하는 API입니다.")
    public ResponseEntity<List<LessonBookedMyTeachersDto>> getMyLessonRequests(
            @AuthenticationPrincipal User sessionStudent) {

        String studentEmail = sessionStudent.getUsername();
        List<LessonBookedMyTeachersDto> myLessonRequests = lessonBookService.getMyLessonRequests(studentEmail);

        return ResponseEntity.ok(myLessonRequests);
    }

    @GetMapping("/received-requests")
    @Operation(summary = "받은 레슨 요청 조회", description = "선생님이 자신에게 온 레슨 요청 목록을 조회하는 API입니다.")
    public ResponseEntity<List<LessonBookedStudentListDto>> getReceivedRequests(
            @AuthenticationPrincipal User sessionTeacher) {

        String teacherEmail = sessionTeacher.getUsername();
        List<LessonBookedStudentListDto> requests = lessonBookService.getReceivedRequests(teacherEmail);

        return ResponseEntity.ok(requests);
    }

    @DeleteMapping("/my-teachers/{lessonBookedId}/cancel")
    @Operation(summary = "내 레슨 요청 취소", description = "학생이 자신이 요청한 레슨을 취소하는 API입니다.")
    public ResponseEntity<String> cancelMyLessonRequest(
            @PathVariable Long lessonBookedId,
            @AuthenticationPrincipal User sessionStudent) {

        String studentEmail = sessionStudent.getUsername();
        lessonBookService.cancelMyLessonRequest(lessonBookedId, studentEmail);

        return ResponseEntity.ok("레슨 요청이 취소되었습니다.");
    }
}