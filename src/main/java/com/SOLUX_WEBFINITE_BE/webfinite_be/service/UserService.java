package com.SOLUX_WEBFINITE_BE.webfinite_be.service;

import com.SOLUX_WEBFINITE_BE.webfinite_be.auth.JwtToken;
import com.SOLUX_WEBFINITE_BE.webfinite_be.auth.JwtTokenProvider;
//import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.User;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.Attend;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.Login;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.User;
import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.SignUpDto;
import com.SOLUX_WEBFINITE_BE.webfinite_be.repository.AttendRepository;
import com.SOLUX_WEBFINITE_BE.webfinite_be.repository.LoginRepository;
import com.SOLUX_WEBFINITE_BE.webfinite_be.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder; // 챗지피티추가

    @Autowired
    private AttendRepository attendRepository;

    @Autowired
    private LoginRepository loginRepository;

    // 로그인
    @Transactional
    public JwtToken signIn(String loginUserId, String password) {

        // 사용자 존재 여부 확인
        Optional<User> optionalUser = userRepository.findByLoginUserId(loginUserId);
        if (optionalUser.isEmpty()) {
            log.error("존재하지 않는 사용자입니다.: {}", loginUserId);
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        User user = optionalUser.get();

        try {
            // Authentication 객체 생성
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginUserId, password);

            // 실제 인증 요청
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

            recordLogin(user.getId()); // 로그인 기록 처리
            return jwtTokenProvider.generateToken(authentication); // 인증이 성공하면 JWT 토큰 생성

        } catch (BadCredentialsException e) {
            // 비밀번호가 틀렸을 경우
            log.error("비밀번호가 틀렸습니다.: {}", loginUserId);
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }
    }

    // 회원가입
    @Transactional
    public void registerUser(SignUpDto signUpDto) {
        // 비밀번호와 비밀번호 확인 일치 여부 확인
        if (!signUpDto.getPassword().equals(signUpDto.getConfirmPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 중복 체크: 아이디와 이메일
        if (userRepository.findByLoginUserId(signUpDto.getLoginUserId()).isPresent()) {
            throw new IllegalStateException("이미 사용 중인 아이디입니다.");
        }
        if (userRepository.findByEmail(signUpDto.getEmail()).isPresent()) {
            throw new IllegalStateException("이미 사용 중인 이메일입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(signUpDto.getPassword());

        // User 엔티티 생성
        User user = User.builder()
                .loginUserId(signUpDto.getLoginUserId())
                .password(encodedPassword)
                .confirmPassword(encodedPassword)
                .plainPassword(signUpDto.getPassword()) // 평문 저장
                .nickname(signUpDto.getNickname())
                .email(signUpDto.getEmail())
                .roles(List.of("USER")) // 기본 권한 부여
                .build();
        // 저장
        userRepository.save(user);
    }

    // 비밀번호 찾기에 사용되는 메서드
    public Optional<User> findUserByLoginUserIdAndEmail(String loginUserId, String email) {
        return userRepository.findByLoginUserIdAndEmail(loginUserId, email);
    }

    public boolean existsByLoginUserId(String loginUserId) {
        return userRepository.findByLoginUserId(loginUserId).isPresent();
    }

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    // 로그인 시 접속 기록과 시간을 Attend, Login에 저장
    @Transactional
    public void recordLogin(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("해당 유저를 찾을 수 없습니다: " + userId);
        }

        User user = optionalUser.get();
        Optional<Attend> optionalAttend = attendRepository.findByUserId(userId);

        Attend attend;
        LocalDate today = LocalDate.now();

        if (optionalAttend.isPresent()) {
            attend = optionalAttend.get();
            if (attend.getAttendDate() != null && ChronoUnit.DAYS.between(attend.getAttendDate(), today) == 1) {
                attend.setAttendDateCnt(attend.getAttendDateCnt() + 1); // 연속 출석 증가
            } else if (attend.getAttendDate() == null || !attend.getAttendDate().isEqual(today)) {
                attend.setAttendDateCnt(1); // 연속 출석 초기화
            }
        } else {
            attend = new Attend();
            attend.setUser(user);
            attend.setAttendDateCnt(1);
        }

        attend.setAttendDate(today);
        attend.setAttended(true);
        attendRepository.save(attend);

        Login login = new Login();
        login.setLoginTime(LocalDateTime.now());
        login.setAttend(attend);
        loginRepository.save(login);
    }

    // 로그아웃 시 로그아웃 시간을 Login에 저장
    @Transactional
    public void recordLogout(Long userId) {
        Optional<Login> optionalLogin = loginRepository.findTopByAttendUserIdOrderByLoginTimeDesc(userId);
        if (optionalLogin.isPresent()) {
            Login login = optionalLogin.get();
            login.setLogoutTime(LocalDateTime.now());
            loginRepository.save(login);
        } else {
            throw new IllegalArgumentException("로그인 기록을 찾을 수 없습니다.");
        }
    }
}
