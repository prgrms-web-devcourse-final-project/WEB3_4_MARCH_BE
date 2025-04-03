package com.backend.global.auth.kakao.util;

import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.util.Date;


/**
 * JWT 토큰 유효성 검사 및 사용자 정보 추출을 담당하는 유틸 클래스
 * JwtUtil과 CookieUtil을 내부적으로 활용함
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProvider {

    @Value("${spring.security.jwt.secret}")
    private String secret;

    private long accessTokenExpiration;

    private long refreshTokenExpiration;

    @Value("${spring.security.jwt.access-token.expiration}")
    private long accessTokenTTL; // ms

    @Value("${spring.security.jwt.refresh-token.expiration}")
    private long refreshTokenTTL; // ms

    // JWT 서명을 위한 Key 생성
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 액세스 토큰 생성 (30분 TTL)
    public String createAccessToken(Long memberId, String role) {
        return createToken(memberId, role, Duration.ofMillis(accessTokenTTL));
    }

    // 리프레시 토큰 생성 (14일 TTL)
    public String createRefreshToken(Long memberId) {
        return createToken(memberId, null, Duration.ofMillis(refreshTokenTTL));
    }

    // JWT 생성 (공통 로직)
    public String createToken(Long memberId, String role, Duration ttl) {
        Date now = new Date();
        var builder = Jwts.builder()
                .claim("id", memberId)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ttl.toMillis()))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256);

        if (role != null) {
            builder.claim("role", role);
        }

        return builder.compact();
    }

    // 토큰에서 사용자 ID 추출
    public Long extractMemberId(String token) {
        return ((Number) parseToken(token).get("id")).longValue();
    }

    // 토큰 유효성 검사 결과(만료 or 변조 여부 확인)를 TokenStatus enum으로 반환
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new GlobalException(GlobalErrorCode.TOKEN_EXPIRED);
        } catch (io.jsonwebtoken.JwtException | IllegalArgumentException e) {
            throw new GlobalException(GlobalErrorCode.INVALID_TOKEN);
        }
    }

    // JWT 파싱
    public Claims parseToken(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

}
