package com.SOLUX_WEBFINITE_BE.webfinite_be.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.security.Key;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Key key;

    // application.yml에서 secret 값 가져와서 key에 저장
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // User 정보를 가지고 AccessToken, RefreshToken을 생성하는 메서드
    public JwtToken generateToken(Authentication authentication) {
        // 권한 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();

        // Authentication에서 userId 추출
        String userId = authentication.getName(); // loadUserByUsername에서 설정한 userId 값

        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + 86400000);
        String accessToken = Jwts.builder()
                .setSubject(userId)
                .claim("auth", authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + 86400000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return JwtToken.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // Jwt 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken) {
        // Jwt 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication return
        // UserDetails: interface, User: UserDetails를 구현한 class
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    // 토큰 정보를 검증하는 메서드
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            log.info("JWT 토큰이 유효합니다."); // 로그아웃 시 확인을 위함
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("유효하지 않은 토큰입니다: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("지원하지 않는 JWT 토큰입니다: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT 클레임 문자열이 비어 있습니다: {}", e.getMessage());
        }
        return false;
    }

    // accessToken에서 Claims 정보를 추출
    private Claims parseClaims(String accessToken) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
            log.info("Claims 추출 성공: {}", claims);
            return claims;
        } catch (ExpiredJwtException e) {
            log.warn("만료된 토큰입니다. Claims 정보 추출: {}", e.getClaims()); // 로그아웃 시 확인을 위함
            return e.getClaims();
        }
    }

    // JwtTokenProvider에서 getClaimsFromToken 추가
    public Claims getClaimsFromToken(String token) {
        return parseClaims(token); // parseClaims를 재사용
    }

    public long getExpirationTime(String token) {
        Claims claims = parseClaims(token);
        return claims.getExpiration().getTime() - System.currentTimeMillis();
    }

    public Long getUserIdFromToken(String token) {

        try {
            Claims claims = getClaimsFromToken(token);
            String subject = claims.getSubject(); // subject에서 userId 추출
            log.info("Claims에서 추출된 subject (userId): {}", subject);
            return Long.parseLong(subject); // String을 Long으로 변환
        } catch (Exception e) {
            log.error("토큰에서 사용자 ID를 추출하지 못했습니다: {}", e.getMessage());
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }
    }
}
