package com.SOLUX_WEBFINITE_BE.webfinite_be.exception;

import com.SOLUX_WEBFINITE_BE.webfinite_be.common.CustomException;
import com.SOLUX_WEBFINITE_BE.webfinite_be.common.ErrorCode;

public class UserNotFoundException extends CustomException {
  public UserNotFoundException() {
    super(ErrorCode.USER_NOT_FOUND);  // TODO_NOT_FOUND ErrorCode 사용
  }
}
