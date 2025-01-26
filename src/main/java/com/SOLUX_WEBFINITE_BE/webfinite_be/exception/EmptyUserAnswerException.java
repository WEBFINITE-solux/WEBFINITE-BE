package com.SOLUX_WEBFINITE_BE.webfinite_be.exception;

import com.SOLUX_WEBFINITE_BE.webfinite_be.common.CustomException;
import com.SOLUX_WEBFINITE_BE.webfinite_be.common.ErrorCode;

public class EmptyUserAnswerException extends CustomException {
  public EmptyUserAnswerException() {
    super(ErrorCode.EMPTY_USER_ANSWER);
  }
}