package com.SOLUX_WEBFINITE_BE.webfinite_be.exception;

import com.SOLUX_WEBFINITE_BE.webfinite_be.common.CustomException;
import com.SOLUX_WEBFINITE_BE.webfinite_be.common.ErrorCode;

public class AlreadySubmittedException extends CustomException {
    public AlreadySubmittedException() {
        super(ErrorCode.ALREADY_EXISTS_QUIZ_ID);
    }
}