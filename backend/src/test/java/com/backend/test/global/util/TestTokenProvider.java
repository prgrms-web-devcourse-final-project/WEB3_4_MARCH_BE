package com.backend.test.global.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import static io.jsonwebtoken.security.Keys.hmacShaKeyFor;

/**
 * 테스트 전용 토큰 발급기
 * 실제 TokenProvider의 구조를 따르며, 테스트 환경(@Profile("test"))에서만 사용된다.
 */
@Component
@Profile("test")
public class TestTokenProvider {

    @Value("${spring.security.jwt.secret}")
    private String secret;

    private SecretKey signingKey;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        this.signingKey = hmacShaKeyFor(keyBytes);
    }

    /**
     * 테스트용 Access Token 생성
     *
     * @param memberId 사용자 ID
     * @param role     사용자 권한 ("ROLE_USER", "ROLE_ADMIN" 등)
     * @return AccessToken 문자열
     */
    public String generateTestAccessToken(Long memberId, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + 1000 * 60 * 10); // 10분 유효 (테스트용)

        return Jwts.builder()
                .claim("id", memberId)
                .claim("role", role)
                .claim("isAdmin", "ROLE_ADMIN".equals(role))
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 테스트용 Refresh Token 생성
     * - UUID 기반으로 생성
     */
    public String generateTestRefreshToken(Long memberId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + 1000 * 60 * 60); // 1시간 유효

        return Jwts.builder()
                .claim("id", memberId)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }
}
