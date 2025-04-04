package com.backend.global.auth.kakao.util;

import com.backend.global.auth.model.CustomUserDetails;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * JWT를 통한 인증 정보를 처리하는 유틸리티 클래스
 * JwtUtil은 JWT를 기반으로 사용자 인증(Authentication) 객체를 생성 (Spring Security와 연결)
 * JWT 파싱, 검증 등의 유틸리티는 TokenProvider가 담당한다.
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final TokenProvider tokenProvider;

    public Authentication getAuthentication(String token) {
        Claims claims = tokenProvider.parseToken(token);

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

//    @Value("${spring.security.jwt.secret}")
//    private String secretKey;
//
//    private Key key;
//
//    @Value("${spring.security.jwt.access-token.expiration}")
//    private long ACCESS_TOKEN_EXPIRATION_TIME; // 6시간 (단위: ms)
//
//    @Value("${spring.security.jwt.refresh-token.expiration}")
//    private long REFRESH_TOKEN_EXPIRATION_TIME; // 60일(약 2달) (단위: ms)
//
//    public Long getAccessTokenExpirationTime() {
//        return ACCESS_TOKEN_EXPIRATION_TIME;
//    }
//
//    public Long getRefreshTokenExpirationTime() {
//        return REFRESH_TOKEN_EXPIRATION_TIME;
//    }
//
//
//    // secretKey를 기반으로 Key key를 초기화
//    @PostConstruct
//    public void init() {
////        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
//        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
//        log.debug("🔑 key hash: {}", Arrays.hashCode(key.getEncoded()));
//
//    }
//
//
//    public String createAccessToken(Long memberId, String email) {
//        return Jwts.builder()
//                .setSubject(email)
//                .claim("id", memberId)
//                .claim("email", email)
//                .claim("role", "ROLE_USER") //사용자의 권한(권리/역할)을 설정 (ROLE_USER면 일반 사용자)
//                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
//                .signWith(key)
//                .compact();
//    }
//
//    public String createRefreshToken(Long memberId) {
//        return Jwts.builder()
//                .setSubject("RefreshToken")
//                .claim("id", memberId)
//                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
//                .signWith(key)
//                .compact();
//    }
//
//    public boolean validateToken(String token) {
//        try {
//            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
//            return true;
//        } catch (ExpiredJwtException e) {
//            log.error("JWT 검증 실패: {}", e.getMessage());
//            throw new GlobalException(GlobalErrorCode.TOKEN_EXPIRED);
//        } catch (MalformedJwtException | SecurityException | UnsupportedJwtException | IllegalArgumentException e) {
//            log.error("JWT 검증 실패: {}", e.getMessage());
//            throw new GlobalException(GlobalErrorCode.INVALID_TOKEN);
//        }
//    }
//
//    public Claims parseToken(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(key)
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }


}
