package com.capstone.bowlingbling.domain.lesson.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonNoteDto {
    private Long teacherId;
    private Long studentId;
    private LocalDateTime lessonDate;
    private String content;
}
