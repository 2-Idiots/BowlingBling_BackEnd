package com.capstone.bowlingbling.domain.lessoninfo.controller;

import com.capstone.bowlingbling.domain.lessoninfo.dto.request.LessonInfoDetailRequestDto;
import com.capstone.bowlingbling.domain.lessoninfo.dto.request.LessonInfoListRequestDto;
import com.capstone.bowlingbling.domain.lessoninfo.dto.response.LessonInfoResponseDto;
import com.capstone.bowlingbling.domain.lessoninfo.service.LessonInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lessonsinfo")
@RequiredArgsConstructor
@Tag(name = "LessonInfo", description = "LessonInfo API")
public class LessonInfoController {

    private final LessonInfoService lessonInfoService;

    @GetMapping
    @Operation(summary = "전체 LessonInfo 목록 조회", description = "페이징된 모든 LessonInfo의 제목, 소개, 볼링장 위치를 반환합니다.")
    public ResponseEntity<Page<LessonInfoListRequestDto>> getAllLessonInfos() {
        Page<LessonInfoListRequestDto> lessons = lessonInfoService.getAllLessonInfos(Pageable.ofSize(10));
        return ResponseEntity.ok(lessons);
    }

    @GetMapping("/{id}")
    @Operation(summary = "LessonInfo 상세 조회", description = "특정 LessonInfo의 상세 정보를 반환합니다.")
    public ResponseEntity<LessonInfoResponseDto> getLessonInfoDetail(
            @Parameter(description = "조회할 LessonInfo의 ID") @PathVariable Long id) {
        LessonInfoResponseDto lessonDetail = lessonInfoService.getLessonInfoDetail(id);
        return ResponseEntity.ok(lessonDetail);
    }

    @PostMapping
    @Operation(summary = "LessonInfo 생성", description = "새로운 LessonInfo를 생성합니다. 권한이 있는 사용자만 사용 가능합니다.")
    public ResponseEntity<LessonInfoResponseDto> createLesson(
            @Parameter(hidden = true) @AuthenticationPrincipal User sessionMember,
            @RequestBody LessonInfoDetailRequestDto request) {

        String teacherEmail = sessionMember.getUsername();
        LessonInfoResponseDto response = lessonInfoService.createLesson(request, teacherEmail);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "LessonInfo 수정", description = "특정 LessonInfo를 수정합니다. 권한이 있는 사용자만 사용 가능합니다.")
    public ResponseEntity<LessonInfoResponseDto> updateLesson(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal User sessionMember,
            @RequestBody LessonInfoDetailRequestDto request) {

        String teacherEmail = sessionMember.getUsername();
        LessonInfoResponseDto response = lessonInfoService.updateLesson(id, request, teacherEmail);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "LessonInfo 삭제", description = "특정 LessonInfo를 삭제합니다. 권한이 있는 사용자만 사용 가능합니다.")
    @ApiResponse(responseCode = "204", description = "성공적으로 삭제되었습니다.")
    public ResponseEntity<Void> deleteLesson(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal User sessionMember) {

        String teacherEmail = sessionMember.getUsername();
        lessonInfoService.deleteLesson(id, teacherEmail);

        return ResponseEntity.noContent().build();
    }
}