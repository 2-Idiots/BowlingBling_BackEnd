package com.capstone.bowlingbling.domain.gathering.domain;

import com.capstone.bowlingbling.global.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@SuperBuilder(toBuilder = true)
@Table(name = "gatherings")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Gathering extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer minAverage;

    @Column(nullable = false)
    private Integer maxAverage;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private Integer maxParticipants;

    @OneToMany(mappedBy = "gathering", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<MemberGathering> memberGatherings = new HashSet<>();

    @Transient
    public int getCurrentParticipants() {
        return memberGatherings.size();
    }
}
