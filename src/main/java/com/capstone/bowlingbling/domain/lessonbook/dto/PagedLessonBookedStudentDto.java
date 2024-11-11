package com.capstone.bowlingbling.domain.lessonbook.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PagedLessonBookedStudentDto {
    private List<LessonBookedStudentDto> content;
    private long totalElements;  // 전체 예약 수
    private int totalPages;      // 전체 페이지 수
    private int size;            // 페이지 크기
    private int number;          // 현재 페이지 번호
}