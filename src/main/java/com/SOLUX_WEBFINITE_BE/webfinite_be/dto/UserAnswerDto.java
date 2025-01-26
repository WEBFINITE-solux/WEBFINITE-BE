package com.SOLUX_WEBFINITE_BE.webfinite_be.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAnswerDto {
    private Long questionId;
    private String userAnswer;
}
