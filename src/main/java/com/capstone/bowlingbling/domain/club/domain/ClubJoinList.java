package com.capstone.bowlingbling.domain.club.domain;

import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.global.BaseEntity;
import com.capstone.bowlingbling.global.enums.ClubRole;
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

    @ManyToOne
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private int averageScore;
    private String experience;
    private String motivation;

    @ElementCollection
    private List<Boolean> availability;

    private ClubRole clubRole;
    private String clubJoinedAt;
    private Integer attendanceRate;
    private String lastAttendance;
    private String inactiveReason;

    @ElementCollection
    private List<Integer> recentScores;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;
}
