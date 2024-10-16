package com.capstone.bowlingbling.domain.lessonrequests.service;

import com.capstone.bowlingbling.domain.lessoninfo.domain.LessonInfo;
import com.capstone.bowlingbling.domain.lessoninfo.repository.LessonInfoRepository;
import com.capstone.bowlingbling.domain.lessonrequests.domain.LessonRequest;
import com.capstone.bowlingbling.domain.lessonrequests.dto.LessonRequestCreateDto;
import com.capstone.bowlingbling.domain.lessonrequests.dto.LessonRequestMyTeachersDto;
import com.capstone.bowlingbling.domain.lessonrequests.dto.LessonRequestStatusDto;
import com.capstone.bowlingbling.domain.lessonrequests.dto.LessonRequestStudentListDto;
import com.capstone.bowlingbling.domain.lessonrequests.repository.LessonRequestRepository;
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
public class LessonRequestService {

    private final LessonRequestRepository lessonRequestRepository;
    private final MemberRepository memberRepository;
    private final LessonInfoRepository lessonInfoRepository;

    @Transactional
    public String createLessonRequest(LessonRequestCreateDto request, String studentEmail) {
        Member student = memberRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new IllegalArgumentException("학생을 찾을 수 없습니다."));
        LessonInfo lessonInfo = lessonInfoRepository.findById(request.getLessonId())
                .orElseThrow(() -> new IllegalArgumentException("레슨 정보를 찾을 수 없습니다."));
        Member teacher = lessonInfo.getMember();

        lessonRequestRepository.findByDayOfWeekAndTimeAndTeacher_Id(request.getDayOfWeek(), request.getTime(), teacher.getId())
                .ifPresent(existing -> {
                    throw new IllegalStateException("해당 시간에는 이미 예약된 수업이 있습니다.");
                });

        LessonRequest lessonRequest = LessonRequest.builder()
                .student(student)
                .teacher(teacher)
                .lessonInfo(lessonInfo)
                .dayOfWeek(request.getDayOfWeek())
                .time(request.getTime())
                .status(RequestStatus.PENDING)
                .build();

        lessonRequestRepository.save(lessonRequest);
        return  lessonInfo.getMember().getName() + "선생님에게" + request.getDayOfWeek() + request.getTime() + "에 예약되었습니다.";
    }

    @Transactional
    public String updateLessonRequestStatus(LessonRequestStatusDto request, String teacherEmail) {
        memberRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new IllegalArgumentException("선생을 찾을 수 없습니다."));

        int updatedCount = lessonRequestRepository.updateStatus(
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
    public List<LessonRequestMyTeachersDto> getMyLessonRequests(String studentEmail) {
        Member student = memberRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new IllegalArgumentException("학생을 찾을 수 없습니다."));

        return lessonRequestRepository.findByStudent(student).stream()
                .map(lessonRequest -> LessonRequestMyTeachersDto.builder()
                        .id(lessonRequest.getId())
                        .teacherName(lessonRequest.getTeacher().getName())
                        .dayOfWeek(lessonRequest.getDayOfWeek())
                        .time(lessonRequest.getTime())
                        .status(lessonRequest.getStatus())
                        .price(lessonRequest.getLessonInfo().getPrice())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public List<LessonRequestStudentListDto> getReceivedRequests(String teacherEmail) {
        Member teacher = memberRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new IllegalArgumentException("선생님을 찾을 수 없습니다."));

        return lessonRequestRepository.findByTeacher(teacher).stream()
                .map(lessonRequest -> LessonRequestStudentListDto.builder()
                        .studentId(lessonRequest.getStudent().getId().toString())
                        .studentName(lessonRequest.getStudent().getName())
                        .dayOfWeek(lessonRequest.getDayOfWeek())
                        .time(lessonRequest.getTime())
                        .accepted(lessonRequest.getStatus())
                        .build())
                .collect(Collectors.toList());
    }
}