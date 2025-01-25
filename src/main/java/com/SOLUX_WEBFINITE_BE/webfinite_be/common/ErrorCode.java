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
    NOT_EXISTS_QUIZ_ID(HttpStatus.NOT_FOUND, "Q-001", "존재하지 않는 퀴즈 아이디입니다."), // 값이 없으면 에러떠서 임시로 넣어둠
    NOT_EXISTS_QUESTION_ID(HttpStatus.NOT_FOUND, "Q-002", "존재하지 않는 질문 아이디입니다."),
    ALREADY_EXISTS_QUIZ_ID(HttpStatus.BAD_REQUEST, "Q-003", "이미 답안을 제출했습니다. 답안을 수정하려면 '답안 수정 기능'을 이용하세요.");


    private final HttpStatus httpStatus;
    private final String errorCode; // 커스텀 에러 코드 -> http status code만으로는 정학한 원인 파악이 어렵기 때문
    private final String message;
}
