package com.SOLUX_WEBFINITE_BE.webfinite_be.exception;

import com.SOLUX_WEBFINITE_BE.webfinite_be.common.CustomException;
import com.SOLUX_WEBFINITE_BE.webfinite_be.common.ErrorCode;

public class EmptyFileContentException extends CustomException {
    public EmptyFileContentException() {
        super(ErrorCode.EMPTY_FILE_CONTENT);
    }
}
