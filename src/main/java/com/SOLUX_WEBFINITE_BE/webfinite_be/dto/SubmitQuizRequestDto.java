package com.SOLUX_WEBFINITE_BE.webfinite_be.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubmitQuizRequestDto {
    private Long userId;
    private Long quizId;  // 퀴즈 ID (추가된 부분)
    private List<UserAnswerDto> answers;
}
