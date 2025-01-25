package com.SOLUX_WEBFINITE_BE.webfinite_be.controller;

import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.AttendResponseDto;
import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.LoginResponseDto;
import com.SOLUX_WEBFINITE_BE.webfinite_be.repository.AttendRepository;
import com.SOLUX_WEBFINITE_BE.webfinite_be.repository.LoginRepository;
import com.SOLUX_WEBFINITE_BE.webfinite_be.service.AttendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController // @RestController = @Controller + @ResponseBody
public class AttendController {

    private final AttendService attendService;
    private final AttendRepository attendRepository;
    private final LoginRepository loginRepository;

    // 출석 여부 조회 (로그인, 로그아웃 YYMMDD 까지 조회) - Attend
    @GetMapping("/attend/{userId}")
    public AttendResponseDto findByUserId(@PathVariable Long userId) {
        return attendService.findByUserId(userId);
    }

    // 학습 달성률 조회 (로그인, 로그아웃 YYMMDD HH:MM:SS 까지 조회) - Login
    @GetMapping("/attend/{userId}/{attendId}")
    public LoginResponseDto findByAttendId(@PathVariable Long attendId, @PathVariable Long userId) {
        return attendService.findByAttendId(attendId);
    }
}
