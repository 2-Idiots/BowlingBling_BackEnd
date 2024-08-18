package com.capstone.bowlingbling.domain.lesson.domain;

import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.global.BaseEntity;
import com.capstone.bowlingbling.global.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder(toBuilder = true)
@Table(name = "lesson_requests")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class LessonRequest extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private Member teacher;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Member student;

    private String requestMessage;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;
}
