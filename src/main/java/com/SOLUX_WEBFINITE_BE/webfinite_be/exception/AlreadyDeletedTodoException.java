package com.SOLUX_WEBFINITE_BE.webfinite_be.exception;

import com.SOLUX_WEBFINITE_BE.webfinite_be.common.CustomException;
import com.SOLUX_WEBFINITE_BE.webfinite_be.common.ErrorCode;

public class AlreadyDeletedTodoException extends CustomException {
  public AlreadyDeletedTodoException() {
    super(ErrorCode.ALREADY_DELETED_TODO);  // TODO_NOT_FOUND ErrorCode 사용
  }
}
