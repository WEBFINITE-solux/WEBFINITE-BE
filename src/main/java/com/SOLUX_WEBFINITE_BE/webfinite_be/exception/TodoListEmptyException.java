package com.SOLUX_WEBFINITE_BE.webfinite_be.exception;

import com.SOLUX_WEBFINITE_BE.webfinite_be.common.CustomException;
import com.SOLUX_WEBFINITE_BE.webfinite_be.common.ErrorCode;

public class TodoListEmptyException extends CustomException {
  public TodoListEmptyException() {
    super(ErrorCode.TODO_LIST_EMPTY);  // TODO_NOT_FOUND ErrorCode 사용
  }
}