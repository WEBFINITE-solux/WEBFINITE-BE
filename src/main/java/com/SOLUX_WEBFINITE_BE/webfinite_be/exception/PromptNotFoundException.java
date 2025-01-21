package com.SOLUX_WEBFINITE_BE.webfinite_be.exception;

import com.SOLUX_WEBFINITE_BE.webfinite_be.common.CustomException;
import com.SOLUX_WEBFINITE_BE.webfinite_be.common.ErrorCode;

public class PromptNotFoundException extends CustomException {
    public PromptNotFoundException() {
        super(ErrorCode.NOT_EXISTS_PROMPT);
    }
}
