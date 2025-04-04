package com.backend.global.auth.interceptor;

import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * ROLE_TEMP_USER 권한을 가진 회원이
 * 주요 기능(API 경로 포함)에 접근하지 못하도록 제어하는 인터셉터.
 * 단, 회원가입 추가정보 입력 경로 등은 허용.
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            boolean isTempUser = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_TEMP_USER"));

            String requestURI = request.getRequestURI();

            // TEMP_USER가 추가정보 입력이 아닌 다른 API에 접근하면 차단
            if (isTempUser && !requestURI.startsWith("/api/members/register")) {
                // 예외 발생
                throw new GlobalException(GlobalErrorCode.TEMP_USER_ACCESS_DENIED);
            }
        }

        return true;
    }
}
