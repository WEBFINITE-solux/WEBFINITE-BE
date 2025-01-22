package com.SOLUX_WEBFINITE_BE.webfinite_be.dto;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.QuizState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QuizListResponseDto {
    private Long quizId;
    private String quizTitle;
    private String courseTitle;  // 강의 제목 필드 추가
    private QuizState quizState;
    private String correctRate;

    public QuizListResponseDto(Long quizId, String quizTitle, String courseTitle, QuizState quizState, String correctRate) {
        this.quizId = quizId;
        this.quizTitle = quizTitle;
        this.courseTitle = courseTitle;
        this.quizState = quizState;
        this.correctRate = correctRate;
    }

    // getters and setters
}

