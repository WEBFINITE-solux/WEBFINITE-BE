package com.SOLUX_WEBFINITE_BE.webfinite_be.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;


/**
 * (ex) NOT_EXISTS_USER_ID(HttpStatus.NOT_FOUND, "U-001", "존재하지 않는 유저 아이디입니다."),
 * 위 예와 같이 에러코드들 Enum으로 관리
 * 형식: errorName(HttpStatus, "errorCode", "message");
 * 에러코드는 관련 기능 이름 첫번째 알파벳-번호 형식으로 작성
 * */

@Getter
@AllArgsConstructor
public enum ErrorCode {

    NOT_EXISTS_USER_ID(HttpStatus.NOT_FOUND, "U-001", "존재하지 않는 유저 아이디입니다."), EMPTY_TODO_CONTENT(HttpStatus.BAD_REQUEST, "T-001", "할 일 내용이 비어 있습니다."),
    TODO_NOT_FOUND(HttpStatus.NOT_FOUND, "'T-002", "할 일을 찾을 수 없습니다."), TODO_LIST_EMPTY(HttpStatus.BAD_REQUEST, "T-003", "TODO 리스트가 비어 있습니다."),
    TODO_DATE_RANGE_EMPTY(HttpStatus.BAD_REQUEST, "T-004", "해당 날짜 범위에 Todo 항목이 없습니다."), USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "U-002", "사용자를 찾을 수 없습니다."),
    ALREADY_DELETED_TODO(HttpStatus.BAD_REQUEST, "T-005", "이미 삭제된 Todo 항목입니다."); // 값이 없으면 에러떠서 임시로 넣어둠
    

    private final HttpStatus httpStatus;
    private final String errorCode; // 커스텀 에러 코드 -> http status code만으로는 정학한 원인 파악이 어렵기 때문
    private final String message;
}
