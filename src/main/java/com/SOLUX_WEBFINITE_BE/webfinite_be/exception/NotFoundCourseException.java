package com.SOLUX_WEBFINITE_BE.webfinite_be.exception;

import com.SOLUX_WEBFINITE_BE.webfinite_be.common.CustomException;
import com.SOLUX_WEBFINITE_BE.webfinite_be.common.ErrorCode;

public class NotFoundCourseException extends CustomException {
    public NotFoundCourseException() {
        super(ErrorCode.NOT_EXISTS_Course_ID);
    }
}
