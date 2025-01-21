package com.SOLUX_WEBFINITE_BE.webfinite_be.exception;

import com.SOLUX_WEBFINITE_BE.webfinite_be.common.CustomException;
import com.SOLUX_WEBFINITE_BE.webfinite_be.common.ErrorCode;

public class EmptyPlanListException extends CustomException {
    public EmptyPlanListException() {
        super(ErrorCode.EMPTY_PLAN_LIST);
    }
}
