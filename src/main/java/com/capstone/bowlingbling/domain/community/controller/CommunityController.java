package com.capstone.bowlingbling.domain.community.controller;

import com.capstone.bowlingbling.domain.community.dto.request.CommunitySaveRequestDto;
import com.capstone.bowlingbling.domain.community.dto.response.CommunityListResponseDto;
import com.capstone.bowlingbling.domain.community.service.CommunityService;
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
@RequestMapping("/community")
public class CommunityController {

    private CommunityService communityService;

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

    @GetMapping
    public Page<CommunityListResponseDto> getAllCommunity() {
        return communityService.getAllCommunity(Pageable.ofSize(10));
    }

    @GetMapping("/searchCategory/{category}")
    public Page<CommunityListResponseDto> getCommunityByCategory(@PathVariable String category){
        return communityService.getCommunityByCategory(category, Pageable.ofSize(10));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> updateCommunity (@PathVariable Long id, @RequestBody CommunitySaveRequestDto communitySaveRequestDto,
                                                   @AuthenticationPrincipal User sessionMember){
        try{
            String memberEmail = sessionMember.getUsername();
            communityService.updateCommunity(id, communitySaveRequestDto, memberEmail);
            return ResponseEntity.ok("게시글이 성공적으로 수정되었습니다");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
