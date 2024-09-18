package com.capstone.bowlingbling.domain.lesson.domain;

import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.global.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Table(name = "lessons")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Lesson extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private Member teacher;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Member student;

    private LocalDateTime lessonDate;

    private String content;
}
