package com.SOLUX_WEBFINITE_BE.webfinite_be.exception;

import com.SOLUX_WEBFINITE_BE.webfinite_be.common.CustomException;
import com.SOLUX_WEBFINITE_BE.webfinite_be.common.ErrorCode;

public class TodoContentEmptyException extends CustomException {
    public TodoContentEmptyException() {
        super(ErrorCode.EMPTY_TODO_CONTENT); // 적절한 ErrorCode 전달
    }
}
