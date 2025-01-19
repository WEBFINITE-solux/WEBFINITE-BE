package com.SOLUX_WEBFINITE_BE.webfinite_be.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 보호 비활성화
        http.csrf(csrf -> csrf.disable());

        // H2 콘솔 접근 허용
        http.authorizeHttpRequests(authz -> authz
                .requestMatchers("/h2-console/**").permitAll()  // H2 콘솔 경로는 인증 없이 접근 가능
                .requestMatchers("/todo/**").permitAll()  // /todo 경로는 인증 없이 접근 가능
                .requestMatchers("/course/**").permitAll()  // course 경로는 인증 없이 접근 가능
                .requestMatchers("/plan/**").permitAll()  // plan 경로는 인증 없이 접근 가능
                .anyRequest().authenticated()  // 나머지 요청은 인증 필요
        );

        // X-Frame-Options 설정: 동일 도메인에서만 iframe을 통해 로드 가능
        http.headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin())
        );

        return http.build();
    }
}

