package com.capstone.bowlingbling.domain.community.domain;

import com.capstone.bowlingbling.domain.comment.domain.CommunityComment;
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
@Table(name = "coummunity")
public class Community extends BaseEntity {
    //TODO place와 image 추가
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    //private boolean anonymous;
    private String category;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String contents;

}