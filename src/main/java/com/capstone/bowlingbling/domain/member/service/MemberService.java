package com.capstone.bowlingbling.domain.member.service;

import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.domain.member.domain.TeacherRequest;
import com.capstone.bowlingbling.domain.member.dto.MemberProfileUpdateRequest;
import com.capstone.bowlingbling.domain.member.dto.TeacherRequestDto;
import com.capstone.bowlingbling.domain.member.repository.MemberRepository;
import com.capstone.bowlingbling.domain.member.repository.TeacherRequestRepository;
import com.capstone.bowlingbling.global.enums.Role;
import com.capstone.bowlingbling.global.enums.TeacherStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final TeacherRequestRepository teacherRequestRepository;

    public MemberService(MemberRepository memberRepository, TeacherRequestRepository teacherRequestRepository) {
        this.memberRepository = memberRepository;
        this.teacherRequestRepository = teacherRequestRepository;
    }

    @Transactional
    public Member updateProfile(MemberProfileUpdateRequest request, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자가 없습니다."));

        if (!member.getEmail().equals(email) && !member.getRole().equals(Role.ADMIN)) {
            throw new SecurityException("삭제 권한이 없습니다.");
        }

        member = member.toBuilder()
                .nickname(request.getNickname() != null ? request.getNickname() : member.getNickname())
                .image(request.getImage() != null ? request.getImage() : member.getImage())
                .phonenum(request.getPhonenum() != null ? request.getPhonenum() : member.getPhonenum())
                .city(request.getCity() != null ? request.getCity() : member.getCity())
                .sex(request.getSex() != null ? request.getSex() : member.getSex())
                .age(request.getAge() != null ? request.getAge() : member.getAge())
                .build();

        return memberRepository.save(member);
    }

    @Transactional
    public TeacherRequest requestTeacherRole(TeacherRequestDto request, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자가 없습니다."));

        TeacherRequest teacherRequest = TeacherRequest.builder()
                .member(member)
                .specialty(request.getSpecialty())
                .bio(request.getBio())
                .status(TeacherStatus.PENDING)
                .build();

        return teacherRequestRepository.save(teacherRequest);
    }

    @Transactional(readOnly = true)
    public Page<TeacherRequest> getPendingTeacherRequests(Pageable pageable) {
        return teacherRequestRepository.findAllByStatus(TeacherStatus.PENDING, pageable);
    }

    @Transactional
    public void approveTeacherRequest(Long requestId) {
        TeacherRequest teacherRequest = teacherRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("해당 요청을 찾을 수 없습니다."));

        Member member = teacherRequest.getMember();
        member = member.toBuilder()
                .role(Role.TEACHER)
                .build();

        teacherRequest = teacherRequest.toBuilder()
                .status(TeacherStatus.APPROVED)
                .build();

        memberRepository.save(member);
        teacherRequestRepository.save(teacherRequest);
    }

    @Transactional(readOnly = true)
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자가 없습니다."));
    }
}
