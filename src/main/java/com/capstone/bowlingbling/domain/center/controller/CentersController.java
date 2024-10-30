package com.capstone.bowlingbling.domain.center.controller;

import com.capstone.bowlingbling.domain.center.dto.request.CenterDetailRequestDto;
import com.capstone.bowlingbling.domain.center.dto.request.CenterSaveRequestDto;
import com.capstone.bowlingbling.domain.center.dto.response.CenterListResponseDto;
import com.capstone.bowlingbling.domain.center.service.CenterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@Tag(name = "centers", description = "볼링장 관리 API")
@RequestMapping("/centers")
public class CentersController {

    private final CenterService centerService;

    public CentersController(CenterService centerService) {
        this.centerService = centerService;
    }

    @Operation(summary = "센터 생성", description = "새로운 센터를 생성합니다.")
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createCenter(@RequestPart("request") CenterSaveRequestDto centerSaveRequestDto,
                                               @AuthenticationPrincipal User sessionUser,
                                               @RequestPart("files") List<MultipartFile> files) throws IOException {
        String memberEmail = sessionUser.getUsername();
        centerService.saveCenter(centerSaveRequestDto, memberEmail, files);
        return new ResponseEntity<>("센터가 성공적으로 생성되었습니다", HttpStatus.CREATED);
    }

    @Operation(summary = "센터 목록 조회", description = "모든 센터를 조회합니다.")
    @GetMapping
    public Page<CenterListResponseDto> getAllCenters(@RequestParam(defaultValue = "0") int page
    ) {
        return centerService.getAllCenters(PageRequest.of(page, 10));
    }

    @Operation(summary = "센터 디테일 조회", description = "아이디에 해당하는 센터를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<CenterDetailRequestDto> getCenterDetails(@PathVariable Long id) {
        CenterDetailRequestDto centerDetails = centerService.getCenterDetails(id);
        return ResponseEntity.ok(centerDetails);
    }

    @Operation(summary = "센터 수정", description = "기존 센터 정보를 수정합니다.")
    @PatchMapping(value = "/{centerId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateCenter(
            @PathVariable Long centerId,
            @RequestPart(value = "request") CenterSaveRequestDto centerSaveRequestDto,
            @Parameter(description = "업로드할 파일 목록", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal User sessionMember) throws IOException {
        try {
            String memberEmail = sessionMember.getUsername();
            centerService.updateCenter(centerId, centerSaveRequestDto, files, memberEmail);
            return new ResponseEntity<>("센터 정보가 성공적으로 수정되었습니다.", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("사용자를 찾을 수 없습니다.", HttpStatus.UNAUTHORIZED);
        }
    }

    @DeleteMapping("/{centerId}")
    public ResponseEntity<String> deleteCenter(@PathVariable Long centerId,
                                               @AuthenticationPrincipal User sessionMember) {
        try {
            String memberEmail = sessionMember.getUsername();
            centerService.deleteCenter(centerId, memberEmail);
            return ResponseEntity.ok("센터가 성공적으로 삭제되었습니다.");
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한이 없습니다.");
        }
    }
}
