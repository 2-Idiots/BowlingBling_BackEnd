package com.capstone.bowlingbling.domain.comment.domain;

import com.capstone.bowlingbling.domain.lessoninfo.domain.LessonInfo;
import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "LessonComments")
public class LessonComment extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private LessonInfo lesson;

    @Column(nullable = false)
    private String comments;
}