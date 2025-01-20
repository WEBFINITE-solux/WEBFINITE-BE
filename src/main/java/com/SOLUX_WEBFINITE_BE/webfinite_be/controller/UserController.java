package com.SOLUX_WEBFINITE_BE.webfinite_be.controller;

import com.SOLUX_WEBFINITE_BE.webfinite_be.auth.JwtToken;
import com.SOLUX_WEBFINITE_BE.webfinite_be.auth.JwtTokenProvider;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.User;
import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.SignInDto;
import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.SignUpDto;
import com.SOLUX_WEBFINITE_BE.webfinite_be.service.JwtLogoutService;
import com.SOLUX_WEBFINITE_BE.webfinite_be.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.Email;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    private final UserService userService;
    private final JwtLogoutService jwtLogoutService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody SignInDto signInDto) {
        try {
            JwtToken jwtToken = userService.signIn(signInDto.getLoginUserId(), signInDto.getPassword());
            return ResponseEntity.ok(jwtToken);
        } catch (IllegalArgumentException e) {
            // 비밀번호 오류 또는 사용자 존재하지 않음
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody SignUpDto signUpDto) {
        try {
            userService.registerUser(signUpDto);
            return ResponseEntity.ok("회원가입이 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

  @PostMapping("/logout")
      public ResponseEntity<String> logout(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {

          System.out.println("Authorization Header: " + authorizationHeader);

          if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
              return ResponseEntity.badRequest().body("유효하지 않은 토큰입니다.");
          }

          String token = authorizationHeader.substring(7); // "Bearer " 제거

          try {
              Long userId = jwtTokenProvider.getUserIdFromToken(token);

              jwtLogoutService.logout(token); // 블랙리스트에 토큰 추가

              userService.recordLogout(userId); // 로그아웃 시간 저장

              return ResponseEntity.ok("로그아웃이 완료되었습니다.");

          } catch (Exception e) {
              return ResponseEntity.status(500).body("로그아웃 처리 중 오류가 발생했습니다.");
          }
      }


    @GetMapping("/password")
    public ResponseEntity<?> findPassword(@RequestParam String loginUserId, @RequestParam String email) {
        if (!userService.existsByLoginUserId(loginUserId)) {
            return ResponseEntity.badRequest().body("아이디가 존재하지 않습니다.");
        }

        if (!userService.existsByEmail(email)) {
            return ResponseEntity.badRequest().body("이메일이 존재하지 않습니다.");
        }

        Optional<User> optionalUser = userService.findUserByLoginUserIdAndEmail(loginUserId, email);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body("아이디와 이메일이 일치하지 않습니다.");
        }

        User user = optionalUser.get();

        // 사용자 정보 반환 (평문 비밀번호 포함)
        return ResponseEntity.ok(Map.of(
                "userId", user.getId(),
                "loginUserId", user.getLoginUserId(),
                "email", user.getEmail(),
                "password", user.getPlainPassword() // 평문 비밀번호 반환
        ));
    }
}