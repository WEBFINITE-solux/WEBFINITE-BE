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
public class QuizDetailResponseDto {
    private Long quizId;  // 퀴즈 ID 추가
    private String quizTitle;  // 퀴즈 제목
    private String courseName;  // 강의명
    private String quizType;  // 퀴즈 유형 추가
    private List<QuestionDetailDto> questions;  // 질문 목록
}