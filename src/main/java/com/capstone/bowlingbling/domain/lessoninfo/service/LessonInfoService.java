package com.capstone.bowlingbling.domain.lessoninfo.service;

import com.capstone.bowlingbling.domain.image.service.S3ImageService;
import com.capstone.bowlingbling.domain.lessoninfo.domain.LessonInfo;
import com.capstone.bowlingbling.domain.lessoninfo.dto.request.LessonInfoCreateDetailRequestDto;
import com.capstone.bowlingbling.domain.lessoninfo.dto.request.LessonInfoDetailUpdateRequestDto;
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
    public void createLesson(LessonInfoCreateDetailRequestDto request, String teacherEmail, List<MultipartFile> files) throws IOException {
        Member teacher = memberRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));

        if (!teacher.getRole().equals(Role.TEACHER) && !teacher.getRole().equals(Role.ADMIN)) {
            throw new IllegalStateException("권한이 없는 사용자입니다.");
        }

        if (lessonInfoRepository.existsByMemberAndDeletedAtIsNull(teacher)) {
            throw new IllegalStateException("레슨글이 이미 존재합니다.");
        }

        // 이미지 업로드 처리 및 URL 받아오기
        List<String> imageUrls = s3ImageService.uploadMultiple(files.toArray(new MultipartFile[0]));

        LessonInfo lessonInfo = LessonInfo.builder()
                .title(request.getTitle())
                .teacherName(teacher.getNickname())
                .intro(request.getIntroduction())
                .contents(request.getContents())
                .qualifications(request.getQualifications())
                .careerHistory(request.getCareerHistory())
                .program(request.getProgram())
                .address(request.getLocation())
                .operatingHours(request.getOperatingHours())
                .images(imageUrls)  // 이미지 URL 저장
                .member(teacher)
                .build();

        lessonInfoRepository.save(lessonInfo);
    }

    // 레슨 수정
    public void updateLesson(Long id, LessonInfoDetailUpdateRequestDto request, String teacherEmail, List<MultipartFile> newImages) throws IOException {
        LessonInfo existingLessonInfo = lessonInfoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 레슨을 찾을 수 없습니다."));

        Member teacher = memberRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!teacher.getRole().equals(Role.TEACHER) && !teacher.getRole().equals(Role.ADMIN)) {
            throw new IllegalStateException("권한이 없습니다.");
        }

        // 새로 업로드된 이미지 처리
        List<String> imageUrls = newImages != null ? s3ImageService.uploadMultiple(newImages.toArray(new MultipartFile[0])) : existingLessonInfo.getImages();

        // LessonInfo 업데이트
        lessonInfoRepository.updateLessonInfo(
                id,
                request.getTitle() != null ? request.getTitle() : existingLessonInfo.getTitle(),
                request.getIntroduction() != null ? request.getIntroduction() : existingLessonInfo.getIntro(),
                request.getContents() != null ? request.getContents() : existingLessonInfo.getContents(),
                request.getQualifications() != null ? request.getQualifications() : existingLessonInfo.getQualifications(),
                request.getCareerHistory() != null ? request.getCareerHistory() : existingLessonInfo.getCareerHistory(),
                request.getProgram() != null ? request.getProgram() : existingLessonInfo.getProgram(),
                request.getLocation() != null ? request.getLocation() : existingLessonInfo.getAddress(),
                request.getOperatingHours() != null ? request.getOperatingHours() : existingLessonInfo.getOperatingHours(),
                imageUrls,
                request.getLat() != null ? request.getLat() : existingLessonInfo.getLat(),
                request.getLng() != null ? request.getLng() : existingLessonInfo.getLng(),
                request.getPlace() != null ? request.getPlace() : existingLessonInfo.getPlace(),
                request.getCategory() != null ? request.getCategory() : existingLessonInfo.getCategory(),
                request.getPrice() != null ? request.getPrice() : existingLessonInfo.getPrice(),
                request.getHasFreeParking() != null ? request.getHasFreeParking() : existingLessonInfo.getHasFreeParking()
        );
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
        lessonInfo.markAsDeleted();
        lessonInfoRepository.save(lessonInfo);
    }

    // 모든 LessonInfo 가져오기 (디테일 포함) deletedAt에 값이 있는 필드 제외
    public Page<LessonInfoResponseDto> getAllLessonInfos(Pageable pageable) {
        return lessonInfoRepository.findAllByDeletedAtIsNull(pageable)
                .map(lesson -> LessonInfoResponseDto.builder()
                        .id(lesson.getId())
                        .title(lesson.getTitle())
                        .introduction(lesson.getIntro())
                        .teacherName(lesson.getTeacherName())
                        .contents(lesson.getContents())
                        .lat(lesson.getLat())
                        .lng(lesson.getLng())
                        .place(lesson.getPlace())
                        .category(lesson.getCategory())
                        .price(lesson.getPrice())
                        .hasFreeParking(lesson.getHasFreeParking())
                        .qualifications(lesson.getQualifications())
                        .careerHistory(lesson.getCareerHistory())
                        .program(lesson.getProgram())
                        .location(lesson.getAddress())
                        .operatingHours(lesson.getOperatingHours())
                        .imageUrls(lesson.getImages())  // 이미지 URL 포함
                        .build());
    }

    // 특정 LessonInfoDetail 가져오기
    public LessonInfoResponseDto getLessonInfoDetail(Long id) {
        LessonInfo lessonInfo = lessonInfoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 레슨입니다."));

        return LessonInfoResponseDto.builder()
                .title(lessonInfo.getTitle())
                .teacherName(lessonInfo.getTeacherName())
                .contents(lessonInfo.getContents())
                .introduction(lessonInfo.getIntro())
                .lat(lessonInfo.getLat())
                .lng(lessonInfo.getLng())
                .place(lessonInfo.getPlace())
                .category(lessonInfo.getCategory())
                .price(lessonInfo.getPrice())
                .hasFreeParking(lessonInfo.getHasFreeParking())
                .qualifications(lessonInfo.getQualifications())
                .careerHistory(lessonInfo.getCareerHistory())
                .program(lessonInfo.getProgram())
                .location(lessonInfo.getAddress())
                .operatingHours(lessonInfo.getOperatingHours())
                .imageUrls(lessonInfo.getImages())  // 이미지 URL 포함
                .build();
    }

    // 레슨 찜하기
    public void likeLesson(String userEmail, Long lessonId) {
        Member member = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));
        LessonInfo lessonInfo = lessonInfoRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("레슨 정보를 찾을 수 없습니다."));

        // 이미 찜한 레슨인지 확인
        if (member.getLikedLessons().contains(lessonInfo)) {
            throw new IllegalArgumentException("이미 찜한 레슨입니다.");
        }

        member.getLikedLessons().add(lessonInfo);  // 찜한 레슨에 추가
        lessonInfo.getLikedMembers().add(member);  // 레슨에 찜한 유저 추가

        // 변경된 엔티티 저장
        memberRepository.save(member);
        lessonInfoRepository.save(lessonInfo);
    }

    // 레슨 찜 취소
    public void cancelLikeLesson(String userEmail, Long lessonId) {
        Member member = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));
        LessonInfo lessonInfo = lessonInfoRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("레슨 정보를 찾을 수 없습니다."));

        member.getLikedLessons().remove(lessonInfo);  // 찜한 레슨에서 삭제
        lessonInfo.getLikedMembers().remove(member);  // 레슨에서 유저 삭제

        // 변경된 엔티티 저장
        memberRepository.save(member);
        lessonInfoRepository.save(lessonInfo);
    }
}