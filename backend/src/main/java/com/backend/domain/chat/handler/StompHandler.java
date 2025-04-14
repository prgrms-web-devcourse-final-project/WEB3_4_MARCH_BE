package com.backend.domain.chat.handler;

import com.backend.global.auth.kakao.util.CookieUtil;
import com.backend.global.auth.kakao.util.JwtUtil;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * StompHandler는 WebSocket 연결 시 STOMP 프로토콜 메시지를 인터셉트하여
 * JWT 토큰을 검증하고, SecurityContext에 인증 정보를 설정하는 역할을 합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil; // 필요하다면 주입

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // STOMP 헤더 액세서로 랩핑
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // 먼저 Authorization 헤더에서 토큰을 확인합니다.
        String token = extractToken(accessor);
        if (token == null) {
            throw new GlobalException(GlobalErrorCode.TOKEN_NOT_FOUND);
        }

        try {
            // 토큰을 이용한 Authentication 생성 후 SecurityContext에 설정
            Authentication authentication = jwtUtil.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            log.error("JWT 인증 처리 중 오류 발생", e);
            throw new GlobalException(GlobalErrorCode.INVALID_TOKEN);
        }
        return message;
    }

    /**
     * STOMP 헤더 또는 쿠키에서 JWT 토큰을 추출합니다.
     * 우선 Authorization 헤더를 확인하고, 없으면 쿠키의 accessToken 값을 확인합니다.
     */
    private String extractToken(StompHeaderAccessor accessor) {
        // 1. Authorization 헤더 확인
        String authHeader = accessor.getFirstNativeHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            log.info("Authorization 헤더에서 토큰을 추출합니다.");
            return authHeader.substring(7);
        }

        // 2. Authorization 헤더에 토큰이 없으면, 쿠키에서 찾기
        // STOMP 메시지에서는 일반적으로 쿠키 헤더도 전달될 수 있습니다.
        List<String> cookieHeaders = accessor.getNativeHeader("Cookie");
        if (cookieHeaders != null) {
            for (String header : cookieHeaders) {
                // 단순 파싱: 쿠키 문자열 예: "accessToken=xxx; 다른쿠키=yyy"
                String[] cookies = header.split(";");
                for (String cookie : cookies) {
                    String[] parts = cookie.trim().split("=");
                    if (parts.length == 2 && "accessToken".equals(parts[0].trim())) {
                        log.info("쿠키에서 accessToken을 추출합니다.");
                        return parts[1].trim();
                    }
                }
            }
        }

        log.warn("토큰을 찾을 수 없습니다.");
        return null;
    }
}