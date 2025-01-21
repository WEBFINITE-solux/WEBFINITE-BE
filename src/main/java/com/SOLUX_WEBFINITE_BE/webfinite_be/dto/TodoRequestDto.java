package com.SOLUX_WEBFINITE_BE.webfinite_be.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TodoRequestDto {

    @JsonProperty("user_id")
    private Long userId;  // userId를 추가

    @JsonProperty("todo_content")
    private String todoContent;

    @JsonProperty("is_completed")
    private boolean isCompleted;  // boolean 타입으로 수정

    @JsonProperty("start_date")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;

    @JsonProperty("end_date")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate;
}
