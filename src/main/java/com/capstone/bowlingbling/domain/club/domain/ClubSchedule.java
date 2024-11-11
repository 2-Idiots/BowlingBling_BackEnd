package com.capstone.bowlingbling.domain.club.domain;

import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.global.BaseEntity;
import com.capstone.bowlingbling.global.enums.Frequency;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Entity
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "club_schedules")
public class ClubSchedule extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;  // 소속된 클럽

    private String title;  // 일정 제목

    @Column(length = 500)
    private String description;  // 상세 설명

    private String location;  // 장소

    @Column(nullable = false)
    private String startDate;  // 시작 일시

    @Column(nullable = false)
    private String endDate;  // 종료 일시

    private Integer maxParticipants;  // 최대 참가 인원 (선택)

    private Boolean isRegular;  // 정기 일정 여부

    private String deadlineDate;  // 참석 마감 기한

    private String cancelableDate;  // 참석 취소 가능 기한

    // 정기 일정 반복 패턴 정보
    @Enumerated(EnumType.STRING)
    private Frequency frequency;  // 반복 주기 (WEEKLY 또는 MONTHLY)

    @ElementCollection
    private List<Integer> daysOfWeek;  // 반복 요일 목록

    private String repeatEndDate;  // 정기 일정 패턴 정보 (빈 값 가능)

    @OneToMany(mappedBy = "clubSchedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScheduleParticipant> participants;  // 참가자 정보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private Member createdBy;  // 일정 생성자
}
