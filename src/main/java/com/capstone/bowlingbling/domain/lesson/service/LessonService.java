package com.capstone.bowlingbling.domain.lesson.service;

import com.capstone.bowlingbling.domain.lesson.domain.Lesson;
import com.capstone.bowlingbling.domain.lesson.domain.LessonRequest;
import com.capstone.bowlingbling.domain.lesson.dto.request.LessonNoteDto;
import com.capstone.bowlingbling.domain.lesson.dto.request.LessonRequestDto;
import com.capstone.bowlingbling.domain.lesson.dto.response.LessonResponseDto;
import com.capstone.bowlingbling.domain.lesson.repository.LessonRepository;
import com.capstone.bowlingbling.domain.lesson.repository.LessonRequestRepository;
import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.domain.member.repository.MemberRepository;
import com.capstone.bowlingbling.global.enums.RequestStatus;
import com.capstone.bowlingbling.global.enums.Role;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LessonService {

    private final LessonRequestRepository lessonRequestRepository;
    private final LessonRepository lessonRepository;
    private final MemberRepository memberRepository;

    public LessonService(LessonRequestRepository lessonRequestRepository, LessonRepository lessonRepository, MemberRepository memberRepository) {
        this.lessonRequestRepository = lessonRequestRepository;
        this.lessonRepository = lessonRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public LessonResponseDto requestLesson(LessonRequestDto requestDto, String studentEmail) {
        Member student = memberRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 학생이 없습니다."));

        Member teacher = memberRepository.findById(requestDto.getTeacherId())
                .orElseThrow(() -> new IllegalArgumentException("해당 아이디를 가진 선생님이 없습니다."));

        if (teacher.getRole() != Role.TEACHER) {
            throw new IllegalArgumentException("해당 아이디는 선생님이 아닙니다.");
        }

        LessonRequest lessonRequest = LessonRequest.builder()
                .teacher(teacher)
                .student(student)
                .requestMessage(requestDto.getRequestMessage())
                .status(RequestStatus.PENDING)
                .build();

        lessonRequestRepository.save(lessonRequest);

        return LessonResponseDto.builder()
                .id(lessonRequest.getId())
                .teacherId(teacher.getId())
                .studentId(student.getId())
                .requestMessage(lessonRequest.getRequestMessage())
                .requestDate(lessonRequest.getCreatedAt())
                .status(lessonRequest.getStatus())
                .build();
    }

    @Transactional(readOnly = true)
    public List<LessonResponseDto> getPendingLessonRequests(String teacherEmail) {
        Member teacher = memberRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 선생님이 없습니다."));

        List<LessonRequest> lessonRequests = lessonRequestRepository.findAllByTeacherIdAndStatus(teacher.getId(), RequestStatus.PENDING);

        return lessonRequests.stream()
                .map(request -> LessonResponseDto.builder()
                        .id(request.getId())
                        .teacherId(request.getTeacher().getId())
                        .studentId(request.getStudent().getId())
                        .requestMessage(request.getRequestMessage())
                        .requestDate(request.getCreatedAt())
                        .status(request.getStatus())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void respondToLessonRequest(Long requestId, boolean isAccepted, String teacherEmail) {
        Member teacher = memberRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 선생님이 없습니다."));

        LessonRequest lessonRequest = lessonRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("해당 요청을 찾을 수 없습니다."));

        if (!lessonRequest.getTeacher().getId().equals(teacher.getId())) {
            throw new IllegalArgumentException("해당 요청은 이 선생님에게 보내진 것이 아닙니다.");
        }

        if (isAccepted) {
            lessonRequest = lessonRequest.toBuilder()
                    .status(RequestStatus.ACCEPTED)
                    .build();

            Lesson lesson = Lesson.builder()
                    .teacher(lessonRequest.getTeacher())
                    .student(lessonRequest.getStudent())
                    .lessonDate(LocalDateTime.now())
                    .content("Initial Lesson")
                    .build();

            lessonRepository.save(lesson);
        } else {
            lessonRequest = lessonRequest.toBuilder()
                    .status(RequestStatus.REJECTED)
                    .build();
        }

        lessonRequestRepository.save(lessonRequest);
    }

    @Transactional
    public void recordLesson(LessonNoteDto lessonDto, String teacherEmail) {
        Member teacher = memberRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 선생님이 없습니다."));

        Member student = memberRepository.findById(lessonDto.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("해당 아이디를 가진 학생이 없습니다."));

        Lesson lesson = Lesson.builder()
                .teacher(teacher)
                .student(student)
                .lessonDate(lessonDto.getLessonDate())
                .content(lessonDto.getContent())
                .build();

        lessonRepository.save(lesson);
    }
}
