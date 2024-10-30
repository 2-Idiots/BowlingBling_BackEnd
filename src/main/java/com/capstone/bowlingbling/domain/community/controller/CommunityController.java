package com.capstone.bowlingbling.domain.community.controller;

import com.capstone.bowlingbling.domain.community.dto.request.CommunitySaveRequestDto;
import com.capstone.bowlingbling.domain.community.dto.response.CommunityListResponseDto;
import com.capstone.bowlingbling.domain.community.service.CommunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
@Tag(name = "communitys", description = "자유게시판 API")
@RequestMapping("/community")
public class CommunityController {

    private final CommunityService communityService;

    public CommunityController(CommunityService communityService) {
        this.communityService = communityService;
    }

    @Operation(summary = "게시글 생성", description = "새로운 게시글을 작성합니다.")
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createBoard(@RequestPart(value = "request") CommunitySaveRequestDto communitySaveRequestDto,
                                              @Parameter(hidden = true) @AuthenticationPrincipal User sessionMember,
                                              @Parameter(description = "업로드할 파일 목록", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
                                              @RequestPart(value = "files", required = false) List<MultipartFile> files) throws IOException {
        try {
            String memberEmail = sessionMember.getUsername();
            communityService.saveCommunity(communitySaveRequestDto, memberEmail, files);
            return new ResponseEntity<>("게시글이 성공적으로 저장되었습니다", HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("사용자가 인증되지 않았습니다", HttpStatus.UNAUTHORIZED);
        }
    }

    @Operation(summary = "게시글 목록 조회", description = "모든 게시글을 조회합니다. 한 번에 10개씩 조회됩니다.")
    @GetMapping
    public Page<CommunityListResponseDto> getAllCommunity(@RequestParam(defaultValue = "0") int page) {
        return communityService.getAllCommunity(PageRequest.of(page, 10));
    }

    @Operation(summary = "카테고리별 게시글 조회", description = "카테고리를 기반으로 게시글을 조회합니다.")
    @GetMapping("/searchCategory/{category}")
    public Page<CommunityListResponseDto> getCommunityByCategory(@PathVariable String category, @RequestParam(defaultValue = "0") int page){
        return communityService.getCommunityByCategory(category, PageRequest.of(page, 10));
    }

    @Operation(summary = "게시글 수정", description = "기존 게시글을 수정합니다.")
    @PatchMapping("/{communityId}")
    public ResponseEntity<String> updateCommunity (@PathVariable Long communityId, @RequestBody CommunitySaveRequestDto communitySaveRequestDto,
                                                   @AuthenticationPrincipal User sessionMember){
        try{
            String memberEmail = sessionMember.getUsername();
            communityService.updateCommunity(communityId, communitySaveRequestDto, memberEmail);
            return ResponseEntity.ok("게시글이 성공적으로 수정되었습니다");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
