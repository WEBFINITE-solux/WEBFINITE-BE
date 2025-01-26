package com.SOLUX_WEBFINITE_BE.webfinite_be.exception;

import com.SOLUX_WEBFINITE_BE.webfinite_be.common.CustomException;
import com.SOLUX_WEBFINITE_BE.webfinite_be.common.ErrorCode;

public class QuizNotFoundException extends CustomException {
    public QuizNotFoundException() {
        super(ErrorCode.NOT_EXISTS_QUIZ_ID); // ErrorCode에서 정의한 항목을 사용
    }
}
