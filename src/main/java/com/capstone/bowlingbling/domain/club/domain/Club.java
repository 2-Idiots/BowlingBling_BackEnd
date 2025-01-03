package com.capstone.bowlingbling.domain.club.domain;

import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "club")
public class Club extends BaseEntity {
    @OneToOne
    private Member leader; //회장
    @Column(nullable = false)
    private String clubName;  //동호회 명
    private String description; //긴 소개글
    private String location;
    private int maxMembers;
    @ElementCollection
    private List<String> meetingDays;
    private String category;
    private int averageScore;
    private String requirements;
    private int monthlyFee;
    private boolean isRecruiting;

    @ElementCollection
    private List<String> images; //동호회 소개 이미지

    @OneToMany(mappedBy = "club", fetch = FetchType.LAZY)
    private List<ClubJoinList> joinList;
}