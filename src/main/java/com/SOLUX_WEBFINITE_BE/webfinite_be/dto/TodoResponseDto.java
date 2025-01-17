package com.SOLUX_WEBFINITE_BE.webfinite_be.dto;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.Todo;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TodoResponseDto {
    @JsonProperty("todo_id")
    private Long todoId;

    @JsonProperty("todo_content")
    private String todoContent;

    @JsonProperty("is_completed")
    private Boolean isCompleted;

    @JsonProperty("start_date")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate; // LocalDateTime으로 반환

    @JsonProperty("end_date")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate; // LocalDateTime으로 반환

    @JsonProperty("user_id")
    private Long userId;

    // Todo 객체를 받아서 DTO로 변환하는 생성자 추가
    public TodoResponseDto(Todo todo) {
        this.todoId = todo.getTodoId();
        this.todoContent = todo.getTodoContent();
        this.isCompleted = todo.getIsCompleted();
        this.startDate = todo.getStartDate();
        this.endDate = todo.getEndDate();
        this.userId = todo.getUserId();
    }
}
