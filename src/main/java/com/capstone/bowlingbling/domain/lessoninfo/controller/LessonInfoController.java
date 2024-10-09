package com.capstone.bowlingbling.domain.lessoninfo.controller;

import com.capstone.bowlingbling.domain.lessoninfo.dto.request.LessonInfoCreateDetailRequestDto;
import com.capstone.bowlingbling.domain.lessoninfo.dto.request.LessonInfoDetailUpdateRequestDto;
import com.capstone.bowlingbling.domain.lessoninfo.dto.response.LessonInfoResponseDto;
import com.capstone.bowlingbling.domain.lessoninfo.service.LessonInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/lesson")
@RequiredArgsConstructor
@Tag(name = "LessonInfo", description = "레슨 상세 API")
public class LessonInfoController {

    private final LessonInfoService lessonInfoService;

    @GetMapping
    @Operation(summary = "전체 LessonInfo 목록 조회", description = "페이징된 모든 LessonInfo의 제목, 소개, 볼링장 위치를 반환합니다.")
    public ResponseEntity<Page<LessonInfoResponseDto>> getAllLessonInfos() {
        Page<LessonInfoResponseDto> lessons = lessonInfoService.getAllLessonInfos(Pageable.ofSize(10));
        return ResponseEntity.ok(lessons);
    }

    @GetMapping("/{id}")
    @Operation(summary = "LessonInfo 상세 조회", description = "특정 LessonInfo의 상세 정보를 반환합니다.")
    public ResponseEntity<LessonInfoResponseDto> getLessonInfoDetail(
            @Parameter(description = "조회할 LessonInfo의 ID") @PathVariable Long id) {
        LessonInfoResponseDto lessonDetail = lessonInfoService.getLessonInfoDetail(id);
        return ResponseEntity.ok(lessonDetail);
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)  // 멀티파트 데이터 타입 지원
    @Operation(
            summary = "LessonInfo 생성",
            description = "새로운 LessonInfo를 생성합니다. 권한이 있는 사용자만 사용 가능합니다."
    )
    public ResponseEntity<LessonInfoResponseDto> createLesson(
            @Parameter(hidden = true) @AuthenticationPrincipal User sessionMember,
            @RequestPart(value = "request") LessonInfoCreateDetailRequestDto request,  // LessonInfo 데이터
            @Parameter(description = "업로드할 파일 목록", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestPart(value = "files", required = false) List<MultipartFile> files) throws IOException {  // 파일 업로드 파트 추가

        String teacherEmail = sessionMember.getUsername();
        LessonInfoResponseDto response = lessonInfoService.createLesson(request, teacherEmail, files);

        return ResponseEntity.ok(response);
    }


    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "LessonInfo 수정", description = "특정 LessonInfo를 수정합니다. 권한이 있는 사용자만 사용 가능합니다.")
    public ResponseEntity<String> updateLesson(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal User sessionMember,
            @RequestPart LessonInfoDetailUpdateRequestDto request,
            @RequestPart(required = false) List<MultipartFile> files) throws IOException {  // 이미지 수정 시 파일도 함께 받을 수 있게 함

        String teacherEmail = sessionMember.getUsername();
        lessonInfoService.updateLesson(id, request, teacherEmail, files);

        return ResponseEntity.ok("수정이 성공적으로 완료되었습니다.");
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