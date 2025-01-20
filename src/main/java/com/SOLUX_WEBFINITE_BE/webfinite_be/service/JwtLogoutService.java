package com.SOLUX_WEBFINITE_BE.webfinite_be.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class JwtLogoutService {

    // 블랙리스트를 메모리에서 관리
    private final Set<String> tokenBlacklist = ConcurrentHashMap.newKeySet();

    // 로그아웃 처리: 토큰 블랙리스트에 추가
    public void logout(String token) {
        tokenBlacklist.add(token);
    }

    // 토큰이 블랙리스트에 있는지 확인
    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklist.contains(token);
    }
}
