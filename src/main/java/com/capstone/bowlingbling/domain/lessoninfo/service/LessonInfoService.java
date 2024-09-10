package com.capstone.bowlingbling.domain.lessoninfo.service;

import com.capstone.bowlingbling.domain.lessoninfo.domain.LessonInfo;
import com.capstone.bowlingbling.domain.lessoninfo.dto.request.LessonInfoDetailRequestDto;
import com.capstone.bowlingbling.domain.lessoninfo.dto.request.LessonInfoListRequestDto;
import com.capstone.bowlingbling.domain.lessoninfo.dto.response.LessonInfoResponseDto;
import com.capstone.bowlingbling.domain.lessoninfo.repository.LessonInfoRepository;
import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.domain.member.repository.MemberRepository;
import com.capstone.bowlingbling.global.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LessonInfoService {

    private final LessonInfoRepository lessonInfoRepository;
    private final MemberRepository memberRepository;

    public LessonInfoResponseDto createLesson(LessonInfoDetailRequestDto request, String teacherEmail) {
        Member teacher = memberRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new IllegalArgumentException("No teacher found"));

        if (!teacher.getRole().equals(Role.TEACHER) && !teacher.getRole().equals(Role.ADMIN)) {
            throw new IllegalStateException("Permission denied");
        }

        if (lessonInfoRepository.existsByMember(teacher)) {
            throw new IllegalStateException("LessonInfo already exists for this teacher.");
        }

        LessonInfo lessonInfo = LessonInfo.builder()
                .title(request.getTitle())
                .teacherName(teacher.getNickname())
                .introduction(request.getIntroduction())
                .qualifications(request.getQualifications())
                .careerHistory(request.getCareerHistory())
                .program(request.getProgram())
                .location(request.getLocation())
                .operatingHours(request.getOperatingHours())
                .member(teacher)
                .build();

        lessonInfoRepository.save(lessonInfo);

        return LessonInfoResponseDto.builder()
                .title(lessonInfo.getTitle())
                .teacherName(teacher.getNickname())
                .introduction(lessonInfo.getIntroduction())
                .qualifications(lessonInfo.getQualifications())
                .careerHistory(lessonInfo.getCareerHistory())
                .program(lessonInfo.getProgram())
                .location(lessonInfo.getLocation())
                .operatingHours(lessonInfo.getOperatingHours())
                .build();
    }

    // 수정 기능
    public LessonInfoResponseDto updateLesson(Long id, LessonInfoDetailRequestDto request, String teacherEmail) {
        LessonInfo lessonInfo = lessonInfoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No lesson found"));

        Member teacher = memberRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new IllegalArgumentException("No teacher found"));

        if (!teacher.getRole().equals(Role.TEACHER) && !teacher.getRole().equals(Role.ADMIN)) {
            throw new IllegalStateException("Permission denied");
        }

        lessonInfo = LessonInfo.builder()
                .title(request.getTitle())
                .introduction(request.getIntroduction())
                .qualifications(request.getQualifications())
                .careerHistory(request.getCareerHistory())
                .program(request.getProgram())
                .location(request.getLocation())
                .operatingHours(request.getOperatingHours())
                .member(teacher)
                .build();

        lessonInfoRepository.save(lessonInfo);

        return LessonInfoResponseDto.builder()
                .title(lessonInfo.getTitle())
                .teacherName(lessonInfo.getTeacherName())
                .introduction(lessonInfo.getIntroduction())
                .qualifications(lessonInfo.getQualifications())
                .careerHistory(lessonInfo.getCareerHistory())
                .program(lessonInfo.getProgram())
                .location(lessonInfo.getLocation())
                .operatingHours(lessonInfo.getOperatingHours())
                .build();
    }

    // 삭제 기능
    public void deleteLesson(Long id, String teacherEmail) {
        LessonInfo lessonInfo = lessonInfoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No lesson found"));

        Member teacher = memberRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new IllegalArgumentException("No teacher found"));

        if (!teacher.getRole().equals(Role.TEACHER) && !teacher.getRole().equals(Role.ADMIN)) {
            throw new IllegalStateException("Permission denied");
        }

        lessonInfoRepository.delete(lessonInfo);
    }

    // 모든 LessonInfo 가져오기 (제목, 내용, 볼링장 위치만 포함)
    public Page<LessonInfoListRequestDto> getAllLessonInfos(Pageable pageable) {
        return lessonInfoRepository.findAll(pageable)
                .map(lesson -> LessonInfoListRequestDto.builder()
                        .title(lesson.getTitle())
                        .introduction(lesson.getIntroduction())
                        .location(lesson.getLocation())
                        .build());
    }

    // 특정 LessonInfoDetail 가져오기
    public LessonInfoResponseDto getLessonInfoDetail(Long id) {
        LessonInfo lessonInfo = lessonInfoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No lesson found"));

        return LessonInfoResponseDto.builder()
                .title(lessonInfo.getTitle())
                .teacherName(lessonInfo.getTeacherName())
                .introduction(lessonInfo.getIntroduction())
                .qualifications(lessonInfo.getQualifications())
                .careerHistory(lessonInfo.getCareerHistory())
                .program(lessonInfo.getProgram())
                .location(lessonInfo.getLocation())
                .operatingHours(lessonInfo.getOperatingHours())
                .build();
    }
}

