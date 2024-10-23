package com.capstone.bowlingbling.domain.lessoninfo.domain;

import com.capstone.bowlingbling.domain.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "liked_lesson")
public class LikedLesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // 좋아요 한 멤버

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lessoninfo_id", nullable = false)
    private LessonInfo lessonInfo; // 좋아요 된 레슨
}