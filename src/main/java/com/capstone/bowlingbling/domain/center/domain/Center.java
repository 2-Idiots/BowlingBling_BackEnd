package com.capstone.bowlingbling.domain.center.domain;

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
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "centers")
public class Center extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Member owner;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String businessName;

    @Column(nullable = false)
    private String operatingHours;

    private String announcements;

    private int laneCount;

    @ElementCollection
    private List<String> images;
}
