package com.backend.global.auth.kakao.util;

import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;


/**
 * JWT 토큰 발급, 토큰 유효성 검사 및 사용자 정보 추출을 담당하는 유틸 클래스
 * JwtUtil과 CookieUtil을 내부적으로 활용함
 * JWT 발급, 파싱, 만료, 유효성 검사, Claim 추출 전담
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProvider {

    @Value("${spring.security.jwt.secret}")
    private String secret;

    @Value("${spring.security.jwt.access-token.expiration}")
    private long accessTokenExpiration; // ms (30분)

    @Value("${spring.security.jwt.refresh-token.expiration}")
    @Getter
    private long refreshTokenExpiration; // ms (14일)

    private Key signingKey;

    // 관리자용 토큰 TTL : 365일
    private static final long adminTokenExpiration = 31536000000L; // ms (365일)

    @PostConstruct
    protected void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    // 액세스 토큰 생성
    public String createAccessToken(Long memberId, String role) {
        // 관리자 계정이면, TTL 365일로 설정
        if ("ROLE_ADMIN".equals(role)) {
            return createToken(memberId, role, Duration.ofMillis(adminTokenExpiration));
        }
        // 일반 사용자면, TTL 30분으로 설정
        return createToken(memberId, role, Duration.ofMillis(accessTokenExpiration));
    }

    // 리프레시 토큰 생성 (14일 TTL)
    // UUID로 jti 생성하여 JWT의 고유 식별자로 설정
    // 매번 refreshToken 발급 시 고유 jti가 부여되므로, 이후 Redis에서 개별 토큰 단위로 관리가 가능
    public String createRefreshToken(Long memberId) {
        String jti = UUID.randomUUID().toString();
        Date now = new Date();
        return Jwts.builder()
                .claim("id", memberId)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenExpiration))
                .setId(jti) // jti 추가
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // JWT 생성 (공통 로직)
    public String createToken(Long memberId, String role, Duration ttl) {
        Date now = new Date();
        var builder = Jwts.builder()
                .claim("id", memberId)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ttl.toMillis()))
                .signWith(signingKey, SignatureAlgorithm.HS256);

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
    public void validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new GlobalException(GlobalErrorCode.TOKEN_EXPIRED);
        } catch (io.jsonwebtoken.JwtException | IllegalArgumentException e) {
            throw new GlobalException(GlobalErrorCode.INVALID_TOKEN);
        }
    }

    // JWT 파싱
    public Claims parseToken(String token) {
        return Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token).getBody();
    }

}
