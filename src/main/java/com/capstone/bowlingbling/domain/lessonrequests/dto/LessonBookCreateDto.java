package com.capstone.bowlingbling.domain.lessonrequests.dto;


import com.capstone.bowlingbling.global.enums.Days;
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
    @JsonProperty("dayofweek")
    private Days dayofweek;
    private String time;
}