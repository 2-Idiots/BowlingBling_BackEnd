package com.capstone.bowlingbling.domain.club.dto.clubSchedule;

import com.capstone.bowlingbling.global.enums.Frequency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RepeatPatternDto {
    private Frequency frequency;  // 반복 주기 (WEEKLY 또는 MONTHLY)
    private List<Integer> daysOfWeek;  // 반복 요일 목록
    private String repeatEndDate;
}
