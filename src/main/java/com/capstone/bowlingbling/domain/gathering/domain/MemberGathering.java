package com.capstone.bowlingbling.domain.gathering.domain;

import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.global.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Table(name = "member_gatherings")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MemberGathering extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gathering_id", nullable = false)
    private Gathering gathering;
}