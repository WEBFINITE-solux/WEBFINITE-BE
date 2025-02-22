package com.SOLUX_WEBFINITE_BE.webfinite_be.config;

import com.SOLUX_WEBFINITE_BE.webfinite_be.auth.JwtAuthenticationFilter;
import com.SOLUX_WEBFINITE_BE.webfinite_be.auth.JwtTokenProvider;
import com.SOLUX_WEBFINITE_BE.webfinite_be.service.CustomUserDetailsService;
import com.SOLUX_WEBFINITE_BE.webfinite_be.service.JwtLogoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtLogoutService jwtLogoutService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 보호 비활성화
        http.csrf(csrf -> csrf.disable());
        // JWT를 사용하기 때문에 세션 비활성화
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        // H2 콘솔 접근 허용
        http.authorizeHttpRequests(authz -> authz
                        .requestMatchers("/h2-console/**").permitAll()  // H2 콘솔 경로는 인증 없이 접근 가능
                        .requestMatchers("/todo/**").permitAll()  // todo 경로는 인증 없이 접근 가능
                        .requestMatchers("/quiz/**").permitAll()  // quiz 경로는 인증 없이 접근 가능
                        .requestMatchers("/course/**").permitAll()  // course 경로는 인증 없이 접근 가능
                        .requestMatchers("/plan/**").permitAll()  // plan 경로는 인증 없이 접근 가능
                        .requestMatchers("/summary/**").permitAll()  // summary 경로는 인증 없이 접근 가능
                        .requestMatchers("/members/sign-in").permitAll() // 로그인 인증 없이 접근 가능
                        .requestMatchers("/user/logout").permitAll() // 로그아웃 인증 없이 접근 가능
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**").permitAll() // 스웨거 인증 없이 접근 허용
//                .anyRequest().authenticated()  // 나머지 요청은 인증 필요
                        .anyRequest().permitAll()  // 나머지 모든 요청을 인증 없이 허가
        );

        // JWT 필터 설정
        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, jwtLogoutService), UsernamePasswordAuthenticationFilter.class); // 생성자 수정

        // X-Frame-Options 설정: 동일 도메인에서만 iframe을 통해 로드 가능
        http.headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin())
        );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt Encoder 사용
    }
}