package com.capstone.bowlingbling.domain.club.domain;

import com.capstone.bowlingbling.global.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubBoardFile extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_board_id", nullable = false)
    private ClubBoard clubBoard;  // 게시글 참조

    private String filename;
    private String fileUrl;
    private Long fileSize;
    private String mimeType;
}