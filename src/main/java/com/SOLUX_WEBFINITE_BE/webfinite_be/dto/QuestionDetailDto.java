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
public class QuestionDetailDto {
    private Long questionId;  // 질문 ID
    private String questionContent;  // 질문 내용
    private List<QuizChoiceDto> choices;  // 선택지 목록
    private String answer;  // 정답
    private String explanation;  // 설명
}
