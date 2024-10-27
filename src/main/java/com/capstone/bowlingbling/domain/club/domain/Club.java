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
    @Column(nullable = false)
    private String introduction; //짧은 소개글
    private String contents; //긴 소개글
    private String thumbNail; //동호회 대표 이미지
    @OneToOne
    private ClubJoinList members; //멤버 리스트
    private int MaxCount;
    private int MinAverage;
    private int dues;
    private String address;
    @ManyToOne
    private Center center;
    private ClubTags clubTags;
}