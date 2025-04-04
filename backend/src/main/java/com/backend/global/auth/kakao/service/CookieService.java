package com.backend.global.auth.kakao.service;

import com.backend.global.auth.kakao.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * JWT 리프레시 토큰과 액세스 토큰을 쿠키에 저장하고, 추출하고, 삭제하는 서비스
 * 내부적으로 CookieUtil을 사용함
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CookieService {

    private final CookieUtil cookieUtil;

    private static final String ACCESS_TOKEN = "accessToken";
    private static final String REFRESH_TOKEN = "refreshToken";

    /**
     * 요청에서 accessToken 쿠키 값을 추출
     */
    public String getAccessTokenFromCookie(HttpServletRequest request) {
        return cookieUtil.getCookieValue(request, ACCESS_TOKEN);
    }

    /**
     * 요청에서 refreshToken 쿠키 값을 추출
     */
    public String getRefreshTokenFromCookie(HttpServletRequest request) {
        return cookieUtil.getCookieValue(request, REFRESH_TOKEN);
    }

    /**
     * accessToken 쿠키 저장 (6시간 유지)
     */
    public void addAccessTokenToCookie(String token, HttpServletResponse response) {
        cookieUtil.addCookie(ACCESS_TOKEN, token, 60 * 60 * 6, response); // 6시간
    }

    /**
     * refreshToken 쿠키 저장 (60일 유지)
     */
    public void addRefreshTokenToCookie(String token, HttpServletResponse response) {
        cookieUtil.addCookie(REFRESH_TOKEN, token, 60 * 60 * 24 * 60, response); // 60일
    }

    /**
     * accessToken, refreshToken 쿠키 삭제
     */
    public void clearTokensFromCookie(HttpServletResponse response) {
        cookieUtil.deleteCookie(ACCESS_TOKEN, response);
        cookieUtil.deleteCookie(REFRESH_TOKEN, response);
    }
}