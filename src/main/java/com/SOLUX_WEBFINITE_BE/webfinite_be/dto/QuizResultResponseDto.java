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
public class QuizResultResponseDto {
    private String message;
    private String quizTitle;
    private String courseName; // 강의명 추가
    private int correctCount;
    private int totalQuestions;
    private String correctRate;
    private List<DetailedResultDto> detailedResults; // 상세 결과 리스트
}
