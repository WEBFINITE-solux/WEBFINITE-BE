package com.SOLUX_WEBFINITE_BE.webfinite_be.exception;

import com.SOLUX_WEBFINITE_BE.webfinite_be.common.CustomException;
import com.SOLUX_WEBFINITE_BE.webfinite_be.common.ErrorCode;

public class EmptyTodoContentException extends CustomException {
    public EmptyTodoContentException() {
        super(ErrorCode.EMPTY_TODO_CONTENT);  // TODO_CONTENT_EMPTY ErrorCode 사용
    }
}

