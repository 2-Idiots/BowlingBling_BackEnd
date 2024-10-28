package com.capstone.bowlingbling.domain.lessonbook.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonBookCreateDto {
    @JsonProperty("lessonid")
    private Long lessonid;
    @JsonProperty("date")
    private String date;
    private String time;
}