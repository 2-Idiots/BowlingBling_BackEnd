package com.capstone.bowlingbling.domain.club.domain;

import com.capstone.bowlingbling.domain.center.domain.Center;
import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.domain.place.domain.Place;
import com.capstone.bowlingbling.global.BaseEntity;
import com.capstone.bowlingbling.global.enums.ClubTags;
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
    @OneToMany(mappedBy = "club")
    private List<ClubJoinList> members;

    @ElementCollection
    private List<String> images; //동호회 소개 이미지

    @OneToOne
    private ClubJoinList joinList; //멤버 리스트
}