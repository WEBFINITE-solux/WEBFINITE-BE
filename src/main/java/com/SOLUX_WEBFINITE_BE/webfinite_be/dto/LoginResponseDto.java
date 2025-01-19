package com.SOLUX_WEBFINITE_BE.webfinite_be.dto;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.Attend;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.Login;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class LoginResponseDto {

    private Long loginId;
    private Long attendId;
    private LocalDateTime loginTime;
    private LocalDateTime logoutTime;

    public LoginResponseDto(Login entity) {
        this.loginId = entity.getId(); // Login의 아이디 (접속 시작, 종료 테이블의 아이디)
        this.attendId = entity.getAttend().getId(); // Attend의 아이디
        this.loginTime = entity.getLoginTime();
        this.logoutTime = entity.getLogoutTime();
    }

}
