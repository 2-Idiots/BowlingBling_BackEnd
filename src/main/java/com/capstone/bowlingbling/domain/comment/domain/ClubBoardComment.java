package com.capstone.bowlingbling.domain.comment.domain;

import com.capstone.bowlingbling.domain.club.domain.ClubBoard;
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
public class ClubBoardComment extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private ClubBoard clubBoard;

    @Column(nullable = false)
    private String comments;
}
