package com.backend.global.auth.kakao.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * 카카오 OAuth 관련 URL을 동적으로 생성하는 유틸 클래스
 * - 인가 코드 요청 URL
 * - 액세스 토큰 요청 URL
 * - 사용자 정보 요청 URL
 * - 리프레시 토큰 갱신 URL
 */

@Component
public class KakaoAuthUtil {

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String REDIRECT_URI;

    @Value("${spring.security.oauth2.client.registration.kakao.authorization-grant-type}")
    private String GRANT_TYPE;

    @Value("${spring.security.oauth2.client.provider.kakao.authorization-uri}")
    private String AUTH_URI;

    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String TOKEN_URI;

    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String USER_INFO_URI;

    @Value("${kakao.logout-url}")
    private String KAKAO_LOGOUT_URL;

    @Value("${kakao.logout-redirect-uri}")
    private String KAKAO_LOGOUT_REDIRECT_URI;

    // 사용자가 카카오 로그인 버튼 클릭 시 리디렉션될 인가 코드 요청 URL
    public String getKakaoAuthorizationUrl() {
        return UriComponentsBuilder.fromUriString(AUTH_URI)
                .queryParam("response_type", "code")
                .queryParam("client_id", CLIENT_ID)
                .queryParam("redirect_uri", REDIRECT_URI)
                .toUriString();
    }

    // 토큰 발급 엔드포인트 URL (쿼리 파라미터 없이 기본 URL만 반환)
    public String getKakaoTokenUrl() {
        return TOKEN_URI;
    }

    // grant type 반환 (예: "authorization_code")
    public String getGrantType() {
        return GRANT_TYPE;
    }

    // client id 반환
    public String getClientId() {
        return CLIENT_ID;
    }

    // redirect URI 반환
    public String getRedirectUri() {
        return REDIRECT_URI;
    }

    // 인가 코드로 카카오 액세스 토큰을 요청하는 URL
    public String getKakaoLoginTokenUrl(String code) {
        return UriComponentsBuilder.fromUriString(TOKEN_URI)
                .queryParam("grant_type", GRANT_TYPE)
                .queryParam("client_id", CLIENT_ID)
                .queryParam("redirect_uri", REDIRECT_URI)
                .queryParam("code", code)
                .toUriString();
    }

    // 사용자 정보 요청 URL
    public String getUserInfoUrl() {
        return USER_INFO_URI;
    }

    // 카카오 리프레시 토큰으로 액세스 토큰 갱신 요청 URL
    public String getKakaoTokenReissueUrl(String refreshToken) {
        return UriComponentsBuilder.fromUriString(TOKEN_URI)
                .queryParam("grant_type", "refresh_token")
                .queryParam("client_id", CLIENT_ID)
                .queryParam("refresh_token", refreshToken)
                .toUriString();
    }

    public String getLogoutUrl(Long userId) {
        return UriComponentsBuilder.fromUriString(KAKAO_LOGOUT_URL)
                .queryParam("client_id", CLIENT_ID)
                .queryParam("logout_redirect_uri", KAKAO_LOGOUT_REDIRECT_URI)
                .queryParam("state", userId)
                .toUriString();
    }
}

