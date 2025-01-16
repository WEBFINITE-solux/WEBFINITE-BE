package com.SOLUX_WEBFINITE_BE.webfinite_be.service;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.User;
import com.SOLUX_WEBFINITE_BE.webfinite_be.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    // 회원 가입
    public Long create(User user) {
        validateDuplicateUser(user); // 아이디 중복 검증

        String encryptedPassword = passwordEncoder.encode(user.getPassword()); // 비밀번호 암호화
        user.setPassword(encryptedPassword); // 암호화된 비밀번호 설정

        userRepository.save(user); // 암호화된 비밀번호와 함께 유저 저장
        return user.getId(); // 저장된 회원의 ID 반환
    }

        // 이메일 중복 확인
        // 아이디 중복 확인

    // 중복 회원 확인
    private void validateDuplicateUser(User user) {
        Optional<User> findUsers = userRepository.findById(user.getId());   // UserRepository에서 같은 아이디가 있는지 조회

        if(findUsers.isPresent()) {   // 회원이 존재한다면 (NullPointerException을 방지, 안전하게 값의 존재 여부를 처리)
            throw new IllegalStateException("이미 존재하는 회원입니다."); // 예외처리 나중에 수정하기
        }
    }

    // 로그인
    public boolean login(long id, String rawPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다.")); // 예외처리 나중에 수정하기

        // 로그인은 userId 확인과 비밀번호 매치로 로그인 성공시키기
        // userId는 회원 가입 시 중복 회원 확인과 로그인에서 사용


        // 입력한 비밀번호와 DB에 저장된 비밀번호를 비교
        if (passwordEncoder.matches(rawPassword, user.getPassword())) {
            return true; // Login successful
        } else {
            throw new IllegalStateException("Invalid password");
        }
    }

    // 로그아웃

    // 비밀번호 찾기 (서버에서 이메일을 바로 보내는 방법을 모르기 때문에 "이메일, 아이디" 조회만)
    public Optional<User> findByLoginUserId(String loginUserId) {
        return userRepository.findByLoginUserId(loginUserId);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
