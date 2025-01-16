package com.SOLUX_WEBFINITE_BE.webfinite_be.controller;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.User;
import com.SOLUX_WEBFINITE_BE.webfinite_be.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.Email;

import java.util.Optional;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
@Validated
public class UserController {

    private final UserService userService;

    // 회원가입 화면 조회
    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm) {
        return "signup_form";
    }

    // 회원가입 등록
    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup_form";
        }

        if (!userCreateForm.getPassword().equals(userCreateForm.getConfirmPassword())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";
        }

        // User 객체 생성
        User user = new User();
        user.setLoginUserId(userCreateForm.getLoginUserId());
        user.setEmail(userCreateForm.getEmail());
        user.setPassword(userCreateForm.getPassword());

        // UserService의 create 메소드 호출 후 등록
        userService.create(user);

        return "redirect:/";
    }

    // 로그인
    @GetMapping("/login")
    public String login() {
        return "login_form";
    }

    // 로그아웃

    // 비밀번호 찾기 (서버에서 이메일을 바로 보내는 방법을 모르기 때문에 "이메일, 아이디" 조회만)
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByLoginUserId(
            @PathVariable @NotBlank(message = "Username cannot be blank") String username) {
        Optional<User> user = userService.findByLoginUserId(username);
        return user.map(ResponseEntity::ok).orElseThrow(); // 추후 UserNotFoundException() 사용
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(
            @PathVariable @Email(message = "Invalid email format") String email) {
        Optional<User> user = userService.findByEmail(email);
        return user.map(ResponseEntity::ok).orElseThrow(); // 추후 UserNotFoundException() 사용
    }

}
