package com.capstone.bowlingbling.domain.club.domain;

import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.global.BaseEntity;
import com.capstone.bowlingbling.global.enums.ClubCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubBoard extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    private String title;

    private String content;

    private ClubCategory clubCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member author;  // 일정 생성자

    private Integer viewCount;
    private Integer commentCount;

    @OneToMany(mappedBy = "clubBoard", cascade = CascadeType.ALL)
    private List<ClubBoardFile> attachments = new ArrayList<>();;

    @Column(nullable = false)
    private Boolean isPinned = false;

    public void incrementViewCount() {
        if (this.viewCount == null) {
            this.viewCount = 1;
        } else {
            this.viewCount += 1;
        }
    }
}
