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
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtLogoutService jwtLogoutService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // REST API이므로 basic auth 및 csrf 보안을 비활성화
                .csrf(csrf -> csrf.disable())
                // JWT를 사용하기 때문에 세션 비활성화
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // URL에 대한 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/members/sign-in").permitAll()
                        .requestMatchers("/user/logout").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**").permitAll() // 스웨거 허용
                        .anyRequest().permitAll()  // 나머지 모든 요청을 인증 없이 허가
                )
                // JWT 필터 설정
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, jwtLogoutService), UsernamePasswordAuthenticationFilter.class); // 생성자 수정
        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt Encoder 사용
    }
}
