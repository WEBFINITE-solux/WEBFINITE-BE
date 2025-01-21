package com.SOLUX_WEBFINITE_BE.webfinite_be.service;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.Attend;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.Login;
import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.AttendResponseDto;
import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.LoginResponseDto;
import com.SOLUX_WEBFINITE_BE.webfinite_be.repository.AttendRepository;
import com.SOLUX_WEBFINITE_BE.webfinite_be.repository.LoginRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AttendService {

    private final AttendRepository attendRepository;
    private final LoginRepository loginRepository;

    // 출석 여부 조회 (로그인, 로그아웃 YYMMDD 까지 조회) - Attend
    @Transactional
    public AttendResponseDto findByUserId(Long userId) {
        Attend entity = attendRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저의 출석 기록이 없습니다. userId = " + userId)); // 예외처리 필요

        return new AttendResponseDto(entity);
    }

    // 학습 달성률 조회 (로그인, 로그아웃 YYMMDD HH:MM:SS 까지 조회) - Login
    @Transactional
    public LoginResponseDto findByAttendId(Long attendId) {
        Login entity = loginRepository.findById(attendId)
                .orElseThrow(() -> new IllegalArgumentException("해당 출석의 시간 기록이 없습니다. attendId = " + attendId)); // 예외처리 필요

        return new LoginResponseDto(entity);
    }
}
