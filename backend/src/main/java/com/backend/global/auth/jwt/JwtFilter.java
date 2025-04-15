package com.backend.global.auth.jwt;

import com.backend.global.auth.kakao.service.CookieService;
import com.backend.global.auth.kakao.util.JwtUtil;
import com.backend.global.auth.kakao.util.TokenProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 인증 필터
 * 매 요청마다 실행되며, 쿠키에서 Access Token을 추출해 유효성 검증 후
 * Spring Security의 SecurityContext에 인증 정보를 저장하는 역할을 한다.
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CookieService cookieService;
    private final TokenProvider tokenProvider;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Actuator health 엔드포인트 로그 출력 건너뛰기
        boolean isHelathEndPoint = request.getRequestURI().contains("/actuator/health");

        // 1. 쿠키에서 AccessToken 추출 (CookieService 활용)
        String token = cookieService.getAccessTokenFromCookie(request);

        // health 엔드포인트가 아닐 때 로그 출력
        if (!isHelathEndPoint) {
            log.info("✅ [JwtFilter] 쿠키로부터 추출한 AccessToken: {}", token);
        }

        // 2. 토큰이 존재하고 유효하면
        if (token != null) {
            try {
                // 2. 토큰에서 claim 추출
                Claims claims = tokenProvider.parseToken(token);
                Boolean isAdmin = claims.get("isAdmin", Boolean.class);

                if (Boolean.TRUE.equals(isAdmin)) {
                    // 2-1. 관리자 토큰인 경우, 일반 토큰 검증을 우회하고 인증 객체 설정
                    // 관리자 토큰인 경우 별도 로그 출력
                    log.info("[JwtFilter] 관리자 토큰 감지: 관리자 계정으로 인증 처리함.");
                    Authentication authentication = jwtUtil.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("🔑 [JwtFilter] 관리자 토큰으로 인증 정보를 설정함.");
                } else {
                    // 2-2. 일반 사용자 토큰인 경우, 유효성 검사 (예외 발생 시 중단)
                    tokenProvider.validateToken(token);

                    // 3-1. JWT로부터 인증(Authentication) 객체 생성
                    Authentication authentication = jwtUtil.getAuthentication(token);

                    // 3-2. SecurityContext에 인증 정보 저장
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                // 인증 실패 → SecurityContextHolder에 아무것도 안 넣고 넘어감
                // 토큰이 유효하지 않아도, Swagger나 로그인 페이지처럼 비회원도 접근해야 하는 리소스에 대한 접근 허용

                // health 엔드포인트가 아닐 때만 출력
                if (!isHelathEndPoint) {
                    log.warn("⚠️ [JwtFilter] JWT 토큰이 유효하지 않음: {}", e.getMessage());
                }
            }
        }
        // 4. 다음 필터로 이동
        filterChain.doFilter(request, response);
    }
}
