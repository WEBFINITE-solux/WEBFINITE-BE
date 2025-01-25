package com.SOLUX_WEBFINITE_BE.webfinite_be.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class JwtToken {

    /**
     * "Bearer" 인증 방식을 사용
     * Access Token을 HTTP 요청의 Authorization 헤더에 포함하여 전송
     * ex) Authorization: Bearer <access_token>
     */

    private String grantType;
    private String accessToken;
    private String refreshToken;
}
