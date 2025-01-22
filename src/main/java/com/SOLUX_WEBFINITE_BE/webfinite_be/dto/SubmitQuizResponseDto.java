package com.SOLUX_WEBFINITE_BE.webfinite_be.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SubmitQuizResponseDto {
    private String message;
    private List<UserAnswerDto> submittedAnswers;

    public SubmitQuizResponseDto(String message, List<UserAnswerDto> submittedAnswers) {
        this.message = message;
        this.submittedAnswers = submittedAnswers;
    }
}
