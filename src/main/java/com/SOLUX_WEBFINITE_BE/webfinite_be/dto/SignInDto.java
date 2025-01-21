package com.SOLUX_WEBFINITE_BE.webfinite_be.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class SignInDto {

    private String loginUserId;
    private String password;
}
