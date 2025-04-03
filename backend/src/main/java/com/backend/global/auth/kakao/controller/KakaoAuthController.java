package com.backend.global.auth.kakao.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.member.entity.Member;
import com.backend.domain.member.service.MemberService;
import com.backend.global.auth.exception.JwtException;
import com.backend.global.auth.kakao.dto.LoginResponseDto;
import com.backend.global.auth.kakao.service.CookieService;
import com.backend.global.auth.kakao.service.KakaoAuthService;
import com.backend.global.auth.kakao.service.RedisRefreshTokenService;
import com.backend.global.auth.kakao.util.TokenProvider;
import com.backend.global.auth.model.CustomUserDetails;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.response.GenericResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 카카오 소셜 로그인 인증을 처리하는 컨트롤러 클래스
 * - 로그인 URL 요청, 로그인 및 회원가입 처리, 토큰 재발급, 로그아웃, 토큰 리프레시 등을 담당
 */

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/kakao")
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;
    private final CookieService cookieService;
    private final RedisRefreshTokenService redisRefreshTokenService;
    private final TokenProvider tokenProvider;
    private final MemberService memberService;

    @Value("${client.base-url}")
    private String clientBaseUrl;

    // 카카오 인가 URL을 프론트에 전달
    @GetMapping("/login-url")
    public ResponseEntity<String> getKakaoLoginUrl() {
        return ResponseEntity.ok(kakaoAuthService.getKakaoAuthorizationUrl());
    }


    /**
     * 로그인 API (인가코드로 로그인 및 회원가입 처리)
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> kakaoLogin(@RequestParam String code, HttpServletResponse response) {
        LoginResponseDto loginResult = kakaoAuthService.processLogin(code, response);
        return ResponseEntity.ok(loginResult);
    }

    /**
     * 프론트 리다이렉트용 콜백 핸들러
     * 로그인 후 콜백 요청 처리 (액세스/리프레시 토큰을 쿠키에 설정)
     */
    @GetMapping("/callback")
    public ResponseEntity<GenericResponse<LoginResponseDto>> loginCallback(@RequestParam String code, HttpServletResponse response) {
        LoginResponseDto login = kakaoAuthService.processLogin(code, response);

        cookieService.addAccessTokenToCookie(login.accessToken(), response);
        cookieService.addRefreshTokenToCookie(login.refreshToken(), response);

        return ResponseEntity.ok(GenericResponse.of(login, "로그인 성공"));
    }

    /**
     * 리프레시 토큰을 통해 새로운 액세스 토큰 재발급
     */
    @PostMapping("/reissue")
    public ResponseEntity<LoginResponseDto> reissue(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = cookieService.getRefreshTokenFromCookie(request);

        Long memberId = tokenProvider.parseToken(refreshToken).get("id", Long.class);

        if (!tokenProvider.validateToken(refreshToken)) {
            throw new JwtException(GlobalErrorCode.INVALID_TOKEN);
        }

        if (redisRefreshTokenService.isValid(memberId, refreshToken)) {
            throw new JwtException(GlobalErrorCode.TOKEN_EXPIRED);
        }

        LoginResponseDto newToken = kakaoAuthService.reissueTokens(refreshToken);

        cookieService.addRefreshTokenToCookie(newToken.refreshToken(), response);
        return ResponseEntity.ok(newToken);
    }

    /**
     * 로그아웃 처리 (리프레시 토큰 삭제, 쿠키 제거)
     */
    @PostMapping("/logout")
    // AuthenticationPrincipal CustomUserDetails를 통해 인증된 사용자 정보 가져옴
    public ResponseEntity<Void> logout(@AuthenticationPrincipal CustomUserDetails userDetails,
                                       HttpServletResponse response) {
        Long memberId = userDetails.getMemberId(); // 인증 정보에서 ID 추출
        redisRefreshTokenService.deleteRefreshToken(memberId);
        cookieService.clearTokensFromCookie(response);
        return ResponseEntity.ok().build();
    }

    /**
     * 쿠키에서 리프레시 토큰을 기반으로 새로운 액세스 토큰 재발급
     */
    @GetMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refreshToken(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                         HttpServletRequest request) {
        String refreshToken = cookieService.getRefreshTokenFromCookie(request);
        Long memberId = userDetails.getMemberId(); // 인증 정보에서 ID 추출

        if (!tokenProvider.validateToken(refreshToken)) {
            throw new JwtException(GlobalErrorCode.INVALID_TOKEN);
        }

        if (!redisRefreshTokenService.isValid(memberId, refreshToken)) {
            throw new JwtException(GlobalErrorCode.TOKEN_EXPIRED);
        }

        String newAccessToken = tokenProvider.createAccessToken(memberId);

        // 필요한 kakaoId 조회
        Member member = memberService.getByKakaoId(memberId);
        return ResponseEntity.ok(LoginResponseDto.of(newAccessToken, member.getKakaoId(), memberId
                , refreshToken, true));
    }

}
