package com.capstone.bowlingbling.domain.lessoninfo.service;

import com.capstone.bowlingbling.domain.image.service.S3ImageService;
import com.capstone.bowlingbling.domain.lessoninfo.domain.LessonInfo;
import com.capstone.bowlingbling.domain.lessoninfo.dto.request.LessonInfoCreateDetailRequestDto;
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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonInfoService {

    private final LessonInfoRepository lessonInfoRepository;
    private final MemberRepository memberRepository;
    private final S3ImageService s3ImageService;

    // 레슨 생성
    public LessonInfoResponseDto createLesson(LessonInfoCreateDetailRequestDto request, String teacherEmail, List<MultipartFile> files) throws IOException {
        Member teacher = memberRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));

        if (!teacher.getRole().equals(Role.TEACHER) && !teacher.getRole().equals(Role.ADMIN)) {
            throw new IllegalStateException("권한이 없는 사용자입니다.");
        }

        if (lessonInfoRepository.existsByMember(teacher)) {
            throw new IllegalStateException("레슨글이 이미 존재합니다.");
        }

        // 이미지 업로드 처리 및 URL 받아오기
        List<String> imageUrls = s3ImageService.uploadMultiple(files.toArray(new MultipartFile[0]));

        LessonInfo lessonInfo = LessonInfo.builder()
                .title(request.getTitle())
                .teacherName(teacher.getNickname())
                .introduction(request.getIntroduction())
                .qualifications(request.getQualifications())
                .careerHistory(request.getCareerHistory())
                .program(request.getProgram())
                .location(request.getLocation())
                .operatingHours(request.getOperatingHours())
                .imageUrls(imageUrls)  // 이미지 URL 저장
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
                .imageUrls(lessonInfo.getImageUrls())  // 응답에도 이미지 URL 포함
                .build();
    }

    // 레슨 수정
    public LessonInfoResponseDto updateLesson(Long id, LessonInfoDetailRequestDto request, String teacherEmail, List<MultipartFile> newImages) throws IOException {
        LessonInfo existingLessonInfo = lessonInfoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 레슨을 찾을 수 없습니다."));

        Member teacher = memberRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!teacher.getRole().equals(Role.TEACHER) && !teacher.getRole().equals(Role.ADMIN)) {
            throw new IllegalStateException("권한이 없습니다.");
        }

        // 새로 업로드된 이미지 처리
        List<String> imageUrls = existingLessonInfo.getImageUrls();
        if (!newImages.isEmpty()) {
            // 기존 이미지 삭제
            for (String imageUrl : imageUrls) {
                String fileName = s3ImageService.extractFileName(imageUrl);
                s3ImageService.deleteFile(fileName);  // S3에서 이미지 삭제
            }

            // 새로운 이미지 업로드
            imageUrls = s3ImageService.uploadMultiple(newImages.toArray(new MultipartFile[0]));
        }

        // LessonInfo 업데이트
        LessonInfo updatedLessonInfo = LessonInfo.builder()
                .id(existingLessonInfo.getId())
                .title(request.getTitle())
                .introduction(request.getIntroduction())
                .qualifications(request.getQualifications())
                .careerHistory(request.getCareerHistory())
                .program(request.getProgram())
                .location(request.getLocation())
                .operatingHours(request.getOperatingHours())
                .member(teacher)
                .imageUrls(imageUrls)  // 새로운 이미지 URL 리스트
                .build();

        lessonInfoRepository.save(updatedLessonInfo);

        return LessonInfoResponseDto.builder()
                .title(updatedLessonInfo.getTitle())
                .teacherName(updatedLessonInfo.getTeacherName())
                .introduction(updatedLessonInfo.getIntroduction())
                .qualifications(updatedLessonInfo.getQualifications())
                .careerHistory(updatedLessonInfo.getCareerHistory())
                .program(updatedLessonInfo.getProgram())
                .location(updatedLessonInfo.getLocation())
                .operatingHours(updatedLessonInfo.getOperatingHours())
                .imageUrls(updatedLessonInfo.getImageUrls())  // 응답에 이미지 URL 포함
                .build();
    }

    // 레슨 삭제
    public void deleteLesson(Long id, String teacherEmail) {
        LessonInfo lessonInfo = lessonInfoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 레슨입니다."));

        Member teacher = memberRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 선생님입니다."));

        if (!teacher.getRole().equals(Role.TEACHER) && !teacher.getRole().equals(Role.ADMIN)) {
            throw new IllegalStateException("권한이 없습니다.");
        }

        // 이미지 삭제
        List<String> imageUrls = lessonInfo.getImageUrls();
        if (imageUrls != null) {
            imageUrls.forEach(imageUrl -> {
                String fileName = s3ImageService.extractFileName(imageUrl);
                s3ImageService.deleteFile(fileName);  // S3에서 이미지 삭제
            });
        }

        lessonInfoRepository.delete(lessonInfo);
    }

    // 모든 LessonInfo 가져오기
    public Page<LessonInfoListRequestDto> getAllLessonInfos(Pageable pageable) {
        return lessonInfoRepository.findAll(pageable)
                .map(lesson -> LessonInfoListRequestDto.builder()
                        .title(lesson.getTitle())
                        .introduction(lesson.getIntroduction())
                        .location(lesson.getLocation())
                        .imageUrl(lesson.getImageUrls().isEmpty() ? null : lesson.getImageUrls().get(0)) // 첫 번째 이미지 URL
                        .build());
    }

    // 특정 LessonInfoDetail 가져오기
    public LessonInfoResponseDto getLessonInfoDetail(Long id) {
        LessonInfo lessonInfo = lessonInfoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 레슨입니다."));

        return LessonInfoResponseDto.builder()
                .title(lessonInfo.getTitle())
                .teacherName(lessonInfo.getTeacherName())
                .introduction(lessonInfo.getIntroduction())
                .qualifications(lessonInfo.getQualifications())
                .careerHistory(lessonInfo.getCareerHistory())
                .program(lessonInfo.getProgram())
                .location(lessonInfo.getLocation())
                .operatingHours(lessonInfo.getOperatingHours())
                .imageUrls(lessonInfo.getImageUrls())  // 이미지 URL 포함
                .build();
    }

}