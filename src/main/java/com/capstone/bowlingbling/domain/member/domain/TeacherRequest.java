package com.capstone.bowlingbling.domain.member.domain;

import com.capstone.bowlingbling.global.BaseEntity;
import com.capstone.bowlingbling.global.enums.TeacherStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder(toBuilder = true)
@Table(name = "teacher_requests")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TeacherRequest extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private String specialty; // 주종목
    private String bio; // 약력

    @Enumerated(EnumType.STRING)
    private TeacherStatus status;
}
