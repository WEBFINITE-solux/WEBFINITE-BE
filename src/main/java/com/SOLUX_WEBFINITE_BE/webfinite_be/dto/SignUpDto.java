package com.SOLUX_WEBFINITE_BE.webfinite_be.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class SignUpDto {

    private String loginUserId;      // 로그인 아이디
    private String password;         // 비밀번호
    private String confirmPassword;  // 비밀번호 확인
    private String nickname;         // 닉네임
    private String email;            // 이메일
}
