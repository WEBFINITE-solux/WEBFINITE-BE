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
    // U - user 관련 에러 코드
    NOT_EXISTS_USER_ID(HttpStatus.NOT_FOUND, "U-001", "존재하지 않는 유저 아이디입니다."),

    // C - Course 관련 에러 코드
    NOT_EXISTS_Course_ID(HttpStatus.NOT_FOUND, "C-001", "존재하지 않는 강의 아이디입니다."),

    // F - File 관련 에러 코드
    NOT_EXISTS_FILE(HttpStatus.NOT_FOUND, "F-001", "존재하지 않는 파일입니다."),
    EMPTY_FILE_CONTENT(HttpStatus.BAD_REQUEST, "F-002", "파일 내용이 비어있습니다."),

    // LP - Plan 관련 에러 코드
    NOT_EXISTS_PLAN_ID(HttpStatus.NOT_FOUND, "LP-001", "존재하지 않는 플랜 아이디입니다."),
    EMPTY_PLAN_LIST(HttpStatus.NOT_FOUND, "LP-002", "학습 계획 목록이 비어있습니다."),

    // P - Prompt 관련 에러 코드
    NOT_EXISTS_PROMPT(HttpStatus.NOT_FOUND, "P-001", "존재하지 않는 프롬프트입니다.");

    private final HttpStatus httpStatus;
    private final String errorCode; // 커스텀 에러 코드 -> http status code만으로는 정학한 원인 파악이 어렵기 때문
    private final String message;
}
