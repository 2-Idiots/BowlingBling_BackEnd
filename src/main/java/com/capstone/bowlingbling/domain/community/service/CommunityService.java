package com.capstone.bowlingbling.domain.community.service;

import com.capstone.bowlingbling.domain.community.domain.Community;
import com.capstone.bowlingbling.domain.community.dto.request.CommunitySaveRequestDto;
import com.capstone.bowlingbling.domain.community.dto.response.CommunityListResponseDto;
import com.capstone.bowlingbling.domain.community.repository.CommunityRepository;
import com.capstone.bowlingbling.domain.image.service.S3ImageService;
import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.domain.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class CommunityService {
    private final CommunityRepository communityRepository;
    private final MemberRepository memberRepository;
    private final S3ImageService s3ImageService;

    @Autowired
    public CommunityService(CommunityRepository communityRepository, MemberRepository memberRepository, S3ImageService s3ImageService){
        this.communityRepository = communityRepository;
        this.memberRepository = memberRepository;
        this.s3ImageService = s3ImageService;
    }

    public void saveCommunity(CommunitySaveRequestDto communityDTO, String memberEmail, List<MultipartFile> files) throws IOException {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자가 없습니다."));

        List<String> imageUrls = s3ImageService.uploadMultiple(files.toArray(new MultipartFile[0]));

        Community community = Community.builder()
                .member(member)
                .title(communityDTO.getTitle())
                .contents(communityDTO.getContents())
                .images(imageUrls)
                .category(communityDTO.getCategory())
                .build();

        communityRepository.save(community);
    }

    public Page<CommunityListResponseDto> getAllCommunity(Pageable pageable){
        Page<Community> communityPage = communityRepository.findAll(pageable);

        return communityPage.map(community -> CommunityListResponseDto.builder()
                .id(community.getId())
                .category(community.getCategory())
                .title(community.getTitle())
                .nickname(community.getMember().getNickname())
                .build());
    }

    public Page<CommunityListResponseDto> getCommunityByCategory(String category, Pageable pageable){
        Page<Community> communityPage = communityRepository.findByCategory(category, pageable);

        return communityPage.map(community -> CommunityListResponseDto.builder()
                .id(community.getId())
                .category(community.getCategory())
                .title(community.getTitle())
                .nickname(community.getMember().getNickname())
                .build());
    }

    public void updateCommunity(Long communityId, CommunitySaveRequestDto communityDTO, String memberEmail){
        memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        communityRepository.findById(communityId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        communityRepository.updateCommunityInfo(communityId, communityDTO.getTitle(), communityDTO.getContents(), communityDTO.getCategory());
    }

}
