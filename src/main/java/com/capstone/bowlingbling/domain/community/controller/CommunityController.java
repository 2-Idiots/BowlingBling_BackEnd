package com.capstone.bowlingbling.domain.community.controller;

import com.capstone.bowlingbling.domain.community.dto.request.CommunitySaveRequestDto;
import com.capstone.bowlingbling.domain.community.dto.response.CommunityListResponseDto;
import com.capstone.bowlingbling.domain.community.service.CommunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "communitys", description = "자유게시판 API")
@RequestMapping("/api/community")
public class CommunityController {

    private CommunityService communityService;

    @Operation(summary = "게시글 생성", description = "새로운 게시글을 작성합니다.")
    @PostMapping("/create")
    public ResponseEntity<String> createBoard(@RequestBody CommunitySaveRequestDto communitySaveRequestDto,
                                              @AuthenticationPrincipal User sessionMember){
        try {
            String memberEmail = sessionMember.getUsername();
            communityService.saveCommunity(communitySaveRequestDto, memberEmail);
            return new ResponseEntity<>("게시글이 성공적으로 저장되었습니다", HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("사용자가 인증되지 않았습니다", HttpStatus.UNAUTHORIZED);
        }
    }

    @Operation(summary = "게시글 목록 조회", description = "모든 게시글을 조회합니다. 한 번에 10개씩 조회됩니다.")
    @GetMapping
    public Page<CommunityListResponseDto> getAllCommunity() {
        return communityService.getAllCommunity(Pageable.ofSize(10));
    }

    @Operation(summary = "카테고리별 게시글 조회", description = "카테고리를 기반으로 게시글을 조회합니다.")
    @GetMapping("/searchCategory/{category}")
    public Page<CommunityListResponseDto> getCommunityByCategory(@PathVariable String category){
        return communityService.getCommunityByCategory(category, Pageable.ofSize(10));
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
