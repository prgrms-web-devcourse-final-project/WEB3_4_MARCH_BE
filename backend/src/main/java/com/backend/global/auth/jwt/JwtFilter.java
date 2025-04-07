package com.backend.global.auth.jwt;

import com.backend.global.auth.kakao.service.CookieService;
import com.backend.global.auth.kakao.util.JwtUtil;
import com.backend.global.auth.kakao.util.TokenProvider;
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

        // 1. 쿠키에서 AccessToken 추출 (CookieService 활용)
        String token = cookieService.getAccessTokenFromCookie(request);
        log.info("✅ [JwtFilter] Extracted AccessToken from Cookie: {}", token);

        // 2. 토큰이 존재하고 유효하면
        if (token != null) {
            tokenProvider.validateToken(token); // 유효성 검사 (예외 발생 시 중단)

            // 2-1. JWT로부터 인증(Authentication) 객체 생성
            Authentication authentication = jwtUtil.getAuthentication(token);

            // 2-2. SecurityContext에 인증 정보 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        // 3. 다음 필터로 이동
        filterChain.doFilter(request, response);
    }
}
