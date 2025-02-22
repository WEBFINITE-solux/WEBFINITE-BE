package com.SOLUX_WEBFINITE_BE.webfinite_be.dto;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizRequestDto {
    private Long userId;  // 사용자 ID
    private Long fileId;  // 파일 ID
    private QuestionType questionType;  // 질문 유형 (예: MULTIPLE_CHOICE, TRUE_FALSE)
    private String quizCountRange;  // 퀴즈 질문 수 범위 (예: "5-7")
    private String detailedRequirements;  // 퀴즈 생성 시 참고할 세부 요구사항
}
