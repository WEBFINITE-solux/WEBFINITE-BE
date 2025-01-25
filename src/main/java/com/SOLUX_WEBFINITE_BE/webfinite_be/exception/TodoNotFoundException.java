package com.SOLUX_WEBFINITE_BE.webfinite_be.exception;

import com.SOLUX_WEBFINITE_BE.webfinite_be.common.CustomException;
import com.SOLUX_WEBFINITE_BE.webfinite_be.common.ErrorCode;

public class TodoNotFoundException extends CustomException {
    public TodoNotFoundException() {
        super(ErrorCode.TODO_NOT_FOUND);  // TODO_NOT_FOUND ErrorCode 사용
    }
}
