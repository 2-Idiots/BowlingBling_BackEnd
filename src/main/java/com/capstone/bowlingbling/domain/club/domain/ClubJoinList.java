package com.capstone.bowlingbling.domain.club.domain;

import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.global.BaseEntity;
import com.capstone.bowlingbling.global.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ClubJoinList extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "club_id")
    private Club club;

    @OneToMany
    @JoinColumn(name = "member_id")
    private List<Member> member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;
}
