package com.capstone.bowlingbling.domain.lessonrequests.controller;

import com.capstone.bowlingbling.domain.lessonrequests.dto.LessonRequestCreateDto;
import com.capstone.bowlingbling.domain.lessonrequests.dto.LessonRequestMyTeachersDto;
import com.capstone.bowlingbling.domain.lessonrequests.dto.LessonRequestStatusDto;
import com.capstone.bowlingbling.domain.lessonrequests.dto.LessonRequestStudentListDto;
import com.capstone.bowlingbling.domain.lessonrequests.service.LessonRequestService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lesson-request")
@RequiredArgsConstructor
public class LessonRequestController {

    private final LessonRequestService lessonRequestService;

    @PostMapping
    public ResponseEntity<String> createLessonRequest(
            @AuthenticationPrincipal User sessionStudent,
            @RequestBody LessonRequestCreateDto request) {

        String studentEmail = sessionStudent.getUsername();
        String result = lessonRequestService.createLessonRequest(request, studentEmail);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/update-status")
    public ResponseEntity<String> updateLessonRequestStatus(
            @AuthenticationPrincipal User sessionTeacher,
            @RequestBody LessonRequestStatusDto request) {

        String teacherEmail = sessionTeacher.getUsername();
        String result = lessonRequestService.updateLessonRequestStatus(request, teacherEmail);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/my-teachers")
    public ResponseEntity<List<LessonRequestMyTeachersDto>> getMyLessonRequests(
            @AuthenticationPrincipal User sessionStudent) {

        String studentEmail = sessionStudent.getUsername();
        List<LessonRequestMyTeachersDto> myLessonRequests = lessonRequestService.getMyLessonRequests(studentEmail);

        return ResponseEntity.ok(myLessonRequests);
    }

    @GetMapping("/received-requests")
    public ResponseEntity<List<LessonRequestStudentListDto>> getReceivedRequests(
            @AuthenticationPrincipal User sessionTeacher) {

        String teacherEmail = sessionTeacher.getUsername();
        List<LessonRequestStudentListDto> requests = lessonRequestService.getReceivedRequests(teacherEmail);

        return ResponseEntity.ok(requests);
    }
}