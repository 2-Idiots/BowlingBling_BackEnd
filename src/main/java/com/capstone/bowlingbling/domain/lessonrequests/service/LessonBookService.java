package com.capstone.bowlingbling.domain.lessonrequests.service;

import com.capstone.bowlingbling.domain.lessoninfo.domain.LessonInfo;
import com.capstone.bowlingbling.domain.lessoninfo.repository.LessonInfoRepository;
import com.capstone.bowlingbling.domain.lessonrequests.domain.LessonBook;
import com.capstone.bowlingbling.domain.lessonrequests.dto.LessonBookCreateDto;
import com.capstone.bowlingbling.domain.lessonrequests.dto.LessonBookedMyTeachersDto;
import com.capstone.bowlingbling.domain.lessonrequests.dto.LessonBookStatusDto;
import com.capstone.bowlingbling.domain.lessonrequests.dto.LessonBookedStudentListDto;
import com.capstone.bowlingbling.domain.lessonrequests.repository.LessonBookRepository;
import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.domain.member.repository.MemberRepository;
import com.capstone.bowlingbling.global.enums.RequestStatus;
import lombok.RequiredArgsConstructor;
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

        lessonBookRepository.findByDayOfWeekAndTimeAndTeacher_Id(request.getDayofweek(), request.getTime(), teacher.getId())
                .ifPresent(existing -> {
                    throw new IllegalStateException("해당 시간에는 이미 예약된 수업이 있습니다.");
                });

        LessonBook lessonBook = LessonBook.builder()
                .student(student)
                .teacher(teacher)
                .lessonInfo(lessonInfo)
                .dayOfWeek(request.getDayofweek())
                .time(request.getTime())
                .status(RequestStatus.PENDING)
                .build();

        lessonBookRepository.save(lessonBook);
        return  lessonInfo.getMember().getName() + " 선생님에게 " + request.getDayofweek() + " " + request.getTime() + " 에 예약되었습니다.";
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
    public List<LessonBookedMyTeachersDto> getMyLessonRequests(String studentEmail) {
        Member student = memberRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new IllegalArgumentException("학생을 찾을 수 없습니다."));

        return lessonBookRepository.findByStudent(student).stream()
                .map(lessonBook -> LessonBookedMyTeachersDto.builder()
                        .id(lessonBook.getId())
                        .teacherName(lessonBook.getTeacher().getName())
                        .dayOfWeek(lessonBook.getDayOfWeek())
                        .time(lessonBook.getTime())
                        .status(lessonBook.getStatus())
                        .price(lessonBook.getLessonInfo().getPrice())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public List<LessonBookedStudentListDto> getReceivedRequests(String teacherEmail) {
        Member teacher = memberRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new IllegalArgumentException("선생님을 찾을 수 없습니다."));

        return lessonBookRepository.findByTeacher(teacher).stream()
                .map(lessonBook -> LessonBookedStudentListDto.builder()
                        .studentId(lessonBook.getStudent().getId().toString())
                        .studentName(lessonBook.getStudent().getName())
                        .dayOfWeek(lessonBook.getDayOfWeek())
                        .time(lessonBook.getTime())
                        .accepted(lessonBook.getStatus())
                        .build())
                .collect(Collectors.toList());
    }
}