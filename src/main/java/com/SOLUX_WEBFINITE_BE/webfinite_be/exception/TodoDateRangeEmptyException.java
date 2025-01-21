package com.SOLUX_WEBFINITE_BE.webfinite_be.exception;

import com.SOLUX_WEBFINITE_BE.webfinite_be.common.CustomException;
import com.SOLUX_WEBFINITE_BE.webfinite_be.common.ErrorCode;

public class TodoDateRangeEmptyException extends CustomException {
    public TodoDateRangeEmptyException() {
        super(ErrorCode.TODO_DATE_RANGE_EMPTY);  // TODO_NOT_FOUND ErrorCode 사용
    }
}
