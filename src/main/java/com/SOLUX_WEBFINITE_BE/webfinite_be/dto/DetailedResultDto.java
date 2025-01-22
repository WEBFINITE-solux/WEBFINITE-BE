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
public class DetailedResultDto {
    private Long questionId;
    private String questionContent;
    private List<String> choices;  // 선택지 리스트
    private String userAnswer;     // 사용자 답변
    private String correctAnswer;  // 정답
    private String explanation;    // 해설
    private boolean isCorrect;     // 정답 여부
}

