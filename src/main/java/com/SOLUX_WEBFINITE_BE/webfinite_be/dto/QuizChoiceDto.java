package com.SOLUX_WEBFINITE_BE.webfinite_be.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizChoiceDto {
    private Long choiceId;
    private String choiceContent;  // 선택지 내용
}
