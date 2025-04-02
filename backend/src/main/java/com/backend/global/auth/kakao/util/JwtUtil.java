package com.backend.global.auth.kakao.util;

import com.backend.global.auth.exception.JwtException;
import com.backend.global.auth.model.CustomUserDetails;
import com.backend.global.exception.GlobalErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

/**
 * JWT를 통한 인증 정보를 처리하는 유틸리티 클래스
 * - 토큰 파싱
 * - 사용자 인증 정보 추출
 * - JWT 관련 예외 처리 담당
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${spring.security.jwt.secret}")
    private String secretKey;

    private Key key;

    @Value("${spring.security.jwt.access-token.expiration}")
    private long ACCESS_TOKEN_EXPIRATION_TIME; // 6시간 (단위: ms)

    @Value("${spring.security.jwt.refresh-token.expiration}")
    private long REFRESH_TOKEN_EXPIRATION_TIME; // 60일(약 2달) (단위: ms)

    public Long getAccessTokenExpirationTime() {
        return ACCESS_TOKEN_EXPIRATION_TIME;
    }

    public Long getRefreshTokenExpirationTime() {
        return REFRESH_TOKEN_EXPIRATION_TIME;
    }

    // secretKey를 기반으로 Key key를 초기화
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }


    public String createAccessToken(Long memberId, String email) {
        return Jwts.builder()
                .setSubject(email)
                .claim("id", memberId)
                .claim("email", email)
                .claim("role", "ROLE_USER")
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(Long memberId) {
        return Jwts.builder()
                .setSubject("RefreshToken")
                .claim("id", memberId)
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new JwtException(GlobalErrorCode.TOKEN_EXPIRED);
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException(GlobalErrorCode.INVALID_TOKEN);
        }
    }

    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseToken(token);

        Long memberId = claims.get("id", Long.class);
        String email = claims.get("email", String.class);
        String role = claims.get("role", String.class);

        CustomUserDetails userDetails = new CustomUserDetails(
                memberId,
                email,
                List.of(new SimpleGrantedAuthority(role))
        );

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
