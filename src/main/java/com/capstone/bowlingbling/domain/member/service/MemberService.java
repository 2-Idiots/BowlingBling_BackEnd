package com.capstone.bowlingbling.domain.member.service;

import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.domain.member.dto.MemberProfileUpdateRequest;
import com.capstone.bowlingbling.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public Member updateProfile(MemberProfileUpdateRequest request, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자가 없습니다."));

        member = member.toBuilder()
                .nickname(request.getNickname() != null ? request.getNickname() : member.getNickname())
                .imageUrl(request.getImageUrl() != null ? request.getImageUrl() : member.getImageUrl())
                .phonenum(request.getPhonenum() != null ? request.getPhonenum() : member.getPhonenum())
                .city(request.getCity() != null ? request.getCity() : member.getCity())
                .sex(request.getSex() != null ? request.getSex() : member.getSex())
                .age(request.getAge() != null ? request.getAge() : member.getAge())
                .build();

        return memberRepository.save(member);
    }
}
