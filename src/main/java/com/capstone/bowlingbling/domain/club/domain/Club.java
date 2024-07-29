package com.capstone.bowlingbling.domain.club.domain;

import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.domain.place.domain.Place;
import com.capstone.bowlingbling.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Entity
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "club")
public class Club extends BaseEntity {
    //TODO place와 image 추가
    @OneToOne
    private Member leader;

    @OneToOne
    @JoinColumn(name = "place_id")
    private Place place;

    @Column(nullable = false)
    private String clubname;

    @Column(nullable = false)
    private String introduction;

    @OneToMany
    private List<Member> members;

    private int memberCount;
}