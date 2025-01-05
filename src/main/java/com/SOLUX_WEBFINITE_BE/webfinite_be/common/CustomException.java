package com.SOLUX_WEBFINITE_BE.webfinite_be.common;

import lombok.Getter;

/**
 * 사용자 정의 예외 클래스
 * 비즈니스 로직에서 발생하는 예외 처리
 */
@Getter
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
