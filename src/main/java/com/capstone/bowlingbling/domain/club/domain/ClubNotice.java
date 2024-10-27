package com.capstone.bowlingbling.domain.club.domain;

import com.capstone.bowlingbling.global.BaseEntity;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubNotice extends BaseEntity {
    private String clubName;
}
