package com.capstone.bowlingbling.domain.lessonbook.service;

import com.capstone.bowlingbling.domain.lessonbook.dto.*;
import com.capstone.bowlingbling.domain.lessoninfo.domain.LessonInfo;
import com.capstone.bowlingbling.domain.lessoninfo.repository.LessonInfoRepository;
import com.capstone.bowlingbling.domain.lessonbook.domain.LessonBook;
import com.capstone.bowlingbling.domain.lessonbook.repository.LessonBookRepository;
import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.domain.member.repository.MemberRepository;
import com.capstone.bowlingbling.global.enums.RequestStatus;
import com.capstone.bowlingbling.global.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonBookService {

    private final LessonBookRepository lessonBookRepository;
    private final MemberRepository memberRepository;
    private final LessonInfoRepository lessonInfoRepository;

    @Transactional
    public String createLessonRequest(LessonBookCreateDto request, String studentEmail) {
        Member student = memberRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new IllegalArgumentException("학생을 찾을 수 없습니다."));
        LessonInfo lessonInfo = lessonInfoRepository.findById(request.getLessonid())
                .orElseThrow(() -> new IllegalArgumentException("레슨 정보를 찾을 수 없습니다."));
        Member teacher = lessonInfo.getMember();

        lessonBookRepository.findByDayOfWeekAndTimeAndTeacher_Id(request.getDate(), request.getTime(), teacher.getId())
                .ifPresent(existing -> {
                    throw new IllegalStateException("해당 시간에는 이미 예약된 수업이 있습니다.");
                });

        LessonBook lessonBook = LessonBook.builder()
                .student(student)
                .teacher(teacher)
                .lessonInfo(lessonInfo)
                .date(request.getDate())
                .time(request.getTime())
                .status(RequestStatus.PENDING)
                .build();

        lessonBookRepository.save(lessonBook);
        return  lessonInfo.getMember().getName() + " 선생님에게 " + request.getDate() + " " + request.getTime() + " 에 예약되었습니다.";
    }

    public List<LessonBookDateTimeDto> getLessonDatesAndTimes(Long lessonInfoId) {
        return lessonBookRepository.findByLessonInfo_IdOrderByCreatedAtDesc(lessonInfoId)
                .stream()
                .map(lessonBook -> LessonBookDateTimeDto.builder()
                        .date(lessonBook.getDate())
                        .time(lessonBook.getTime())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public String updateLessonRequestStatus(LessonBookStatusDto request, String teacherEmail) {
        memberRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new IllegalArgumentException("선생을 찾을 수 없습니다."));

        int updatedCount = lessonBookRepository.updateStatus(
                request.getRequestId(),
                request.getStatus(),
                teacherEmail
        );

        if (updatedCount == 0) {
            throw new SecurityException("해당 요청을 수정할 권한이 없거나 레슨 신청 내용을 찾을 수 없습니다.");
        }

        return "상태가" + request.getStatus() + "으로 변경 되었습니다.";
    }

    @Transactional
    public LessonBookStatusResponseDto approveLessonRequest(Long bookingId, String teacherEmail) {
        memberRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new IllegalArgumentException("선생님을 찾을 수 없습니다."));

        LessonBook lessonBook = lessonBookRepository.findByIdAndTeacherEmail(bookingId, teacherEmail)
                .orElseThrow(() -> new SecurityException("해당 요청을 수정할 권한이 없거나 레슨 신청 내용을 찾을 수 없습니다."));

        lessonBookRepository.updateStatus(
                bookingId,
                RequestStatus.CONFIRMED,
                teacherEmail
        );

        return LessonBookStatusResponseDto.builder()
                .id(lessonBook.getId())
                .status(lessonBook.getStatus().name())
                .updatedAt(lessonBook.getModifiedAt().toString())
                .message("레슨 예약이 승인되었습니다.")
                .build();
    }

    @Transactional
    public LessonBookStatusResponseDto rejectLessonRequest(Long bookingId, String teacherEmail, String reason) {
        memberRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new IllegalArgumentException("선생님을 찾을 수 없습니다."));

        LessonBook lessonBook = lessonBookRepository.findByIdAndTeacherEmail(bookingId, teacherEmail)
                .orElseThrow(() -> new SecurityException("해당 요청을 수정할 권한이 없거나 레슨 신청 내용을 찾을 수 없습니다."));

        lessonBookRepository.updateStatus(
                bookingId,
                RequestStatus.CANCELLED,
                teacherEmail
        );

        return LessonBookStatusResponseDto.builder()
                .id(lessonBook.getId())
                .status(lessonBook.getStatus().name())
                .updatedAt(lessonBook.getModifiedAt().toString())
                .message("레슨 예약이 거절되었습니다. 사유: " + reason)
                .build();
    }


    @Transactional
    public List<LessonBookedMyTeachersDto> getMyLessonRequests(String studentEmail) {
        Member student = memberRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new IllegalArgumentException("학생을 찾을 수 없습니다."));

        return lessonBookRepository.findByStudent(student).stream()
                .map(lessonBook -> LessonBookedMyTeachersDto.builder()
                        .id(lessonBook.getId())
                        .lessonId(lessonBook.getLessonInfo().getId())
                        .teacherName(lessonBook.getTeacher().getName())
                        .date(lessonBook.getDate())
                        .time(lessonBook.getTime())
                        .status(lessonBook.getStatus())
                        .price(lessonBook.getLessonInfo().getPrice())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public PagedLessonBookedStudentDto getReceivedRequests(String teacherEmail, int page, int size) {
        Member teacher = memberRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new IllegalArgumentException("선생님을 찾을 수 없습니다."));

        Pageable pageable = PageRequest.of(page, size);
        Page<LessonBook> lessonBookPage = lessonBookRepository.findByTeacher(teacher, pageable);

        List<LessonBookedStudentDto> content = lessonBookPage.getContent().stream()
                .map(lessonBook -> LessonBookedStudentDto.builder()
                        .id(lessonBook.getId())
                        .lessonId(lessonBook.getLessonInfo().getId())
                        .studentName(lessonBook.getStudent().getName())
                        .date(lessonBook.getDate())
                        .time(lessonBook.getTime())
                        .status(lessonBook.getStatus())
                        .price(lessonBook.getLessonInfo().getPrice()) // 가격 정보 추가
                        .createdAt(lessonBook.getCreatedAt().toString()) // 예약 생성 시간 추가
                        .build())
                .collect(Collectors.toList());

        return PagedLessonBookedStudentDto.builder()
                .content(content)
                .totalElements(lessonBookPage.getTotalElements())
                .totalPages(lessonBookPage.getTotalPages())
                .size(lessonBookPage.getSize())
                .number(lessonBookPage.getNumber())
                .build();
    }

    public void cancelMyLessonRequest(Long lessonBookId, String userEmail) {
        Member member = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));
        LessonBook lessonBook = lessonBookRepository.findById(lessonBookId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 레슨 예약입니다. : " + lessonBookId));

        if (!member.getEmail().equals(lessonBook.getStudent().getEmail()) && !member.getRole().equals(Role.ADMIN)) {
            throw new IllegalStateException("권한이 없는 사용자입니다.");
        }

        lessonBookRepository.delete(lessonBook);
    }
}