package com.SOLUX_WEBFINITE_BE.webfinite_be.exception;

import com.SOLUX_WEBFINITE_BE.webfinite_be.common.CustomException;
import com.SOLUX_WEBFINITE_BE.webfinite_be.common.ErrorCode;

public class CourseNotFoundException extends CustomException {
    public CourseNotFoundException() {
        super(ErrorCode.NOT_EXISTS_Course_ID);
    }
}