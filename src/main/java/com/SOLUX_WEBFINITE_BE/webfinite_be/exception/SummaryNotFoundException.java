package com.SOLUX_WEBFINITE_BE.webfinite_be.exception;

import com.SOLUX_WEBFINITE_BE.webfinite_be.common.CustomException;
import com.SOLUX_WEBFINITE_BE.webfinite_be.common.ErrorCode;

public class SummaryNotFoundException extends CustomException {
    public SummaryNotFoundException() {
        super(ErrorCode.SUMMARY_NOT_FOUND);
    }
}
