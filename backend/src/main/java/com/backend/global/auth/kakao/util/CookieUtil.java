package com.backend.global.auth.kakao.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 쿠키 관련 기능을 제공하는 유틸리티 클래스
 * - 요청에서 쿠키 값을 조회
 * - 응답에 쿠키 추가
 * - 쿠키 삭제
 */

@Component
@Slf4j
public class CookieUtil {

    /**
     * 요청에 포함된 쿠키 중에서 지정한 이름의 쿠키 값을 반환
     * @param request 요청 객체
     * @param name 찾을 쿠키 이름
     * @return 쿠키 값 또는 null
     */
//    public String getCookieValue(HttpServletRequest request, String name) {
//        if (request.getCookies() == null) return null;
//
//        for (Cookie cookie : request.getCookies()) {
//            if (name.equals(cookie.getName())) {
//                return cookie.getValue();
//            }
//        }
//        return null;
//    }
    public String getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            log.warn("⚠️ [CookieUtil] 요청에 쿠키가 존재하지 않습니다.");
            return null;
        }

        for (Cookie cookie : cookies) {
            log.info("🔍 [CookieUtil] 쿠키 확인: name={}, value={}", cookie.getName(), cookie.getValue());
            if (cookie.getName().equals(name)) {
                log.info("✅ [CookieUtil] Target cookie '{}' 를 찾았습니다.", name);
                return cookie.getValue();
            }
        }

        log.warn("❌ [CookieUtil] 요청에서 쿠키 '{}' 를 찾을 수 없습니다.", name);
        return null;
    }

    /**
     * 쿠키를 생성하고 응답에 추가
     * @param name 쿠키 이름
     * @param value 쿠키 값
     * @param maxAge 쿠키 만료 시간 (초 단위)
     * @param response 응답 객체
     */
    public void addCookie(String name, String value, long maxAge, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true); // 쿠키를 브라우저의 JavaScript에서 접근할 수 없도록 설정
//        cookie.setSecure(true); // HTTPS 환경에서만 쿠키 전송
        cookie.setSecure(false); //스웨거 테스트용 임시 설정
        cookie.setAttribute("SameSite", "None");
        cookie.setPath("/");
        cookie.setMaxAge((int) maxAge);
        response.addCookie(cookie);
    }

    /**
     * 지정한 이름의 쿠키를 삭제 (유효시간 0으로 설정)
     * @param name 삭제할 쿠키 이름
     * @param response 응답 객체
     */
    public void deleteCookie(String name, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, null);
        cookie.setMaxAge(0); // 즉시 만료
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setAttribute("SameSite", "None");
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
