package com.capstone.bowlingbling.domain.gathering.domain;

import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.domain.place.domain.Place;
import com.capstone.bowlingbling.global.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@SuperBuilder(toBuilder = true)
@Table(name = "gatherings")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Gathering extends BaseEntity {

    @OneToOne
    private Member leader;

    private String title;

    private Integer minAverage;

    private Integer maxAverage;

    private String description;

    private String location;

    private LocalDateTime date;

    private Integer maxParticipants;

    @ElementCollection
    private List<String> images;

    private String lat;
    private String lng;

    @OneToOne
    @JoinColumn(name = "place_id")
    private Place place;

    @OneToMany(mappedBy = "gathering", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<MemberGathering> memberGatherings = new HashSet<>();

    @Transient
    public int getCurrentParticipants() {
        return memberGatherings.size();
    }
}
