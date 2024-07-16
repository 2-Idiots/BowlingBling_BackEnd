package com.capstone.bowlingbling.domain.community.service;

import com.capstone.bowlingbling.domain.community.domain.Community;
import com.capstone.bowlingbling.domain.community.dto.request.CommunitySaveRequestDto;
import com.capstone.bowlingbling.domain.community.dto.response.CommunityListResponseDto;
import com.capstone.bowlingbling.domain.community.repository.CommunityRepository;
import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.domain.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CommunityService {
    private CommunityRepository communityRepository;
    private MemberRepository memberRepository;

    @Autowired
    public CommunityService(CommunityRepository communityRepository, MemberRepository memberRepository){
        this.communityRepository = communityRepository;
        this.memberRepository = memberRepository;
    }

    public void saveCommunity(CommunitySaveRequestDto communityDTO, String memberEmail){
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자가 없습니다."));

        Community community = Community.builder()
                .member(member)
                .title(communityDTO.getTitle())
                .contents(communityDTO.getContents())
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
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        community = community.toBuilder()
                .title(communityDTO.getTitle() != null ? communityDTO.getTitle() : community.getTitle())
                .contents(communityDTO.getContents() != null ? communityDTO.getContents() : community.getContents())
                .category(communityDTO.getCategory() != null ? communityDTO.getCategory() : community.getCategory())
                .build();

        communityRepository.save(community);
    }

}
