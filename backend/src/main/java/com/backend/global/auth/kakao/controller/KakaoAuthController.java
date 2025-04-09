package com.backend.global.auth.kakao.controller;

import com.backend.domain.member.entity.Member;
import com.backend.domain.member.service.MemberService;
import com.backend.global.auth.kakao.dto.LoginResponseDto;
import com.backend.global.auth.kakao.service.CookieService;
import com.backend.global.auth.kakao.service.KakaoAuthService;
import com.backend.global.auth.kakao.service.RedisRefreshTokenService;
import com.backend.global.auth.kakao.util.TokenProvider;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import com.backend.global.response.GenericResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


//    /**
//     * 로그인 API (인가코드로 로그인 및 회원가입 처리)
//     * 	프론트에서 code 받아 백엔드로 POST
//     */
//    @PostMapping("/login")
//    public ResponseEntity<LoginResponseDto> kakaoLogin(@RequestParam String code, HttpServletResponse response) {
//        LoginResponseDto loginResult = kakaoAuthService.processLogin(code, response);
//        cookieService.addAccessTokenToCookie(loginResult.accessToken(), response);
//        cookieService.addRefreshTokenToCookie(loginResult.refreshToken(), response);
//        return ResponseEntity.ok(loginResult);
//    }

    /**
     * 프론트 리다이렉트용 콜백 핸들러
     * 로그인 후 콜백 요청 처리 (액세스/리프레시 토큰을 쿠키에 설정)
     * 카카오 서버가 직접 리다이렉트
     * 로그인 후 콜백 시 HttpServletRequest 추가
     */
    @GetMapping("/callback")
    public ResponseEntity<GenericResponse<LoginResponseDto>> loginCallback(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String error,
            @RequestParam(required = false, name = "error_description") String errorDescription,
            HttpServletRequest request,
            HttpServletResponse response) {

        // 카카오 로그인 실패 시 예외 처리
        if (error != null) {
            throw new GlobalException(GlobalErrorCode.KAKAO_LOGIN_FAILED,
                    "카카오 로그인 실패: " + (errorDescription != null ? errorDescription : error));
        }

        // 카카오 로그인 정상 처리시
        LoginResponseDto loginResult = kakaoAuthService.processLogin(code, request, response);

        cookieService.addAccessTokenToCookie(loginResult.accessToken(), response);
        cookieService.addRefreshTokenToCookie(loginResult.refreshToken(), response);

        return ResponseEntity.ok(GenericResponse.of(loginResult, "카카오 로그인 성공"));
    }

    /**
     * 리프레시 토큰을 통해 새로운 액세스 토큰 재발급
     */
    @PostMapping("/reissue")
    public ResponseEntity<GenericResponse<LoginResponseDto>> reissue(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = cookieService.getRefreshTokenFromCookie(request);
        tokenProvider.validateToken(refreshToken);

        Long memberId = tokenProvider.parseToken(refreshToken).get("id", Long.class);
        String jti = tokenProvider.parseToken(refreshToken).getId();
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        // Redis에서 저장된 refreshToken과 일치하는지 확인 (화이트리스트 방식)
        if (!redisRefreshTokenService.isValid(memberId, jti, refreshToken, ip, userAgent)) {
            throw new GlobalException(GlobalErrorCode.TOKEN_EXPIRED);
        }

        // reissueTokens 메서드에 HttpServletRequest 전달
        LoginResponseDto newToken = kakaoAuthService.reissueTokens(refreshToken, request);

        // 리프레시 토큰이 갱신되었다면 쿠키에 반영
        if (newToken.refreshToken() != null) {
            cookieService.addRefreshTokenToCookie(newToken.refreshToken(), response);
        }

        return ResponseEntity.ok(GenericResponse.of(newToken, "토큰 재발급 성공"));
    }

    /**
     * 로그아웃 처리 (리프레시 토큰 삭제, 쿠키 제거)
     */
    @PostMapping("/logout")
    // AuthenticationPrincipal CustomUserDetails를 통해 인증된 사용자 정보 가져옴
    public ResponseEntity<GenericResponse<Void>> logout(HttpServletRequest request,
                                                        HttpServletResponse response) {

        String refreshToken = cookieService.getRefreshTokenFromCookie(request);

        // 이미 로그아웃되었거나 쿠키가 존재하지 않는 상황이므로,
        // 별도의 삭제 로직 없이 쿠키를 비워주고 로그아웃 성공 메시지를 반환
        if (refreshToken == null) {

            cookieService.clearTokensFromCookie(response);
            return ResponseEntity.ok(GenericResponse.<Void>ok("로그아웃 성공"));
        }

        // refresh token에서 memberId 추출
        Long memberId = tokenProvider.extractMemberId(refreshToken);
        String jti = tokenProvider.parseToken(refreshToken).getId();

        // Redis에 저장된 refresh token 삭제
        redisRefreshTokenService.deleteRefreshToken(memberId, jti);
        log.info("[Logout] 로그아웃한 회원 {}에 대한 리프레시 토큰(jti: {}) 삭제 완료.", memberId, jti);

        cookieService.clearTokensFromCookie(response);
        return ResponseEntity.ok(GenericResponse.<Void>ok("로그아웃 성공"));
    }

    /**
     * 쿠키에서 리프레시 토큰을 기반으로 새로운 액세스 토큰 재발급
     */
    @GetMapping("/refresh")
    public ResponseEntity<GenericResponse<LoginResponseDto>> refreshToken(HttpServletRequest request,
                                                                          HttpServletResponse response) {
        String refreshToken = cookieService.getRefreshTokenFromCookie(request);
        tokenProvider.validateToken(refreshToken);

        // 기존의 만료된 access token 대신 쿠키에 저장된 refresh token을 기반으로 새 토큰을 발급
        // refresh token에서 memberId 추출
        Long memberId = tokenProvider.extractMemberId(refreshToken);
        String jti = tokenProvider.parseToken(refreshToken).getId();
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        // Redis에서 저장된 refreshToken과 일치하는지 확인 (화이트리스트 방식)
        if (!redisRefreshTokenService.isValid(memberId, jti, refreshToken, ip, userAgent)) {
            throw new GlobalException(GlobalErrorCode.INVALID_TOKEN); // 같지 않으면 예외 처리
        }

        // 회원 정보를 기본키(memberId)를 기준으로 조회
        Member member = memberService.getMemberEntity(memberId);
        String role = member.getRole().name();

        // 새로운 AccessToken 발급
        String newAccessToken = tokenProvider.createAccessToken(memberId, role);

        // 새로운 RefreshToken도 발급  (덮어쓰기)
        String newRefreshToken = tokenProvider.createRefreshToken(memberId);
        long refreshTokenExpiration = tokenProvider.getRefreshTokenExpiration(); // refresh token 유효기간 가져오기

        // 새로운 jti 및 클라이언트 정보 발급
        String newJti = tokenProvider.parseToken(newRefreshToken).getId();

        // 새 refresh token 저장 및 쿠키 업데이트
        redisRefreshTokenService.saveRefreshToken(memberId, newJti, newRefreshToken, refreshTokenExpiration, ip, userAgent);
        cookieService.addRefreshTokenToCookie(newRefreshToken, response);

        return ResponseEntity.ok(GenericResponse.of(
                LoginResponseDto.of(newAccessToken, member.getKakaoId(), memberId, newRefreshToken, true),
                "토큰 리프레쉬 성공"
        ));
    }
}


