package com.SOLUX_WEBFINITE_BE.webfinite_be.exception;

import com.SOLUX_WEBFINITE_BE.webfinite_be.common.CustomException;
import com.SOLUX_WEBFINITE_BE.webfinite_be.common.ErrorCode;

public class PlanNotFoundException extends CustomException {
    public PlanNotFoundException() {
        super(ErrorCode.NOT_EXISTS_PLAN_ID);
    }
}
