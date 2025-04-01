package com.backend.global.auth.kakao.service;

import com.backend.domain.member.dto.MemberRegisterRequestDto;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.domain.member.service.MemberService;
import com.backend.global.auth.kakao.dto.KakaoTokenResponseDto;
import com.backend.global.auth.kakao.dto.KakaoUserInfoResponseDto;
import com.backend.global.auth.kakao.dto.LoginResponseDto;
import com.backend.global.auth.kakao.util.JwtUtil;
import com.backend.global.auth.kakao.util.KakaoAuthUtil;
import com.backend.global.auth.kakao.util.TokenProvider;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 카카오 인가 코드를 받아 회원 정보를 조회하고 JWT 토큰을 발급하는 인증 서비스
 * 회원이 존재하지 않으면 자동으로 회원가입 진행
 * 발급한 리프레시 토큰은 Redis와 쿠키에 저장
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoAuthService {
    // 카카오 API 호출 관련 유틸
    private final KakaoAuthUtil kakaoAuthUtil;
    private final WebClient webClient;

    // JWT 관련 유틸
    private final TokenProvider tokenProvider;
    private final JwtUtil jwtUtil;

    // 회원 등록/조회 서비스
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    // Redis에 리프레시 토큰 저장
    private final RedisRefreshTokenService redisRefreshTokenService;

    // 쿠키 관련 서비스 및 유틸
    private final CookieService cookieService;

    /**
     * 카카오 로그인 인가 요청 URL 반환
     */
    public String getKakaoAuthorizationUrl() {
        return kakaoAuthUtil.getKakaoAuthorizationUrl();
    }

    /**
     * 인가 코드를 통해 카카오로부터 토큰 발급받기
     */
    public KakaoTokenResponseDto getTokenFromKakao(String code) {
        return webClient.post()
                .uri(kakaoAuthUtil.getKakaoLoginTokenUrl(code))
                .retrieve()
                .bodyToMono(KakaoTokenResponseDto.class)
                .block();
    }

    /**
     * 카카오 accessToken을 사용해 사용자 정보 조회
     */
    public KakaoUserInfoResponseDto getUserInfo(String accessToken) {
        return webClient.get()
                .uri(kakaoAuthUtil.getUserInfoUrl())
                .headers(h -> {
                    h.setBearerAuth(accessToken);
                    h.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                })
                .retrieve()
                .bodyToMono(KakaoUserInfoResponseDto.class)
                .block();
    }

    /**
     * 카카오 인가 코드를 통해 로그인 또는 회원가입 처리 후 JWT 토큰 발급
     */
    // 회원 조회 및 회원가입
    public LoginResponseDto processLogin(String code, HttpServletResponse response) {
        // 1. 인가 코드로 accessToken, refreshToken 발급받기
        KakaoTokenResponseDto kakaoTokenDto = getTokenFromKakao(code);
        String kakaoAccessToken = kakaoTokenDto.accessToken();
        String kakaoRefreshToken = kakaoTokenDto.refreshToken();

        // 2. 카카오 accessToken으로 사용자 정보 요청
        KakaoUserInfoResponseDto kakaoUserInfo = getUserInfo(kakaoAccessToken);
        Long kakaoId = kakaoUserInfo.id();

        // 3. 해당 kakaoId가 등록된 사용자인지 확인 (회원인지 아닌지 확인)
        Member member = memberRepository.findByKakaoId(kakaoId).orElse(null);

        if (member == null) {
            // 3-1. 신규 사용자라면 registerMember 호출
            MemberRegisterRequestDto registerDto = MemberRegisterRequestDto.fromKakao(kakaoUserInfo);
            memberService.registerMember(registerDto);

            // 등록 후 다시 조회
            member = memberRepository.findByKakaoId(kakaoId)
                    .orElseThrow(() -> new GlobalException(GlobalErrorCode.MEMBER_REGISTRATION_FAILED));
        } else {
            // 3-2. 기존 회원이면 카카오 토큰 업데이트
            member.updateAccessToken(kakaoAccessToken);
            member.updateRefreshToken(kakaoRefreshToken);
        }

        // 4. JWT access, refresh 토큰 생성
        String accessToken = tokenProvider.createAccessToken(member.getId());
        String refreshToken = tokenProvider.createRefreshToken(member.getId());

        // 5. Redis에 리프레시 토큰 저장 (중복 로그인 방지)
        long ttl = jwtUtil.getRefreshTokenExpirationTime();
        redisRefreshTokenService.saveRefreshToken(member.getId(), refreshToken, ttl);

        // 6. 리프레시 토큰을 쿠키에 저장
        cookieService.addRefreshTokenToCookie(refreshToken, response);

        // 7. 응답 DTO 반환
        return LoginResponseDto.of(accessToken, member.getId(), refreshToken);
    }

    /**
     * 카카오 리프레시 토큰을 통해 accessToken 재발급
     */
    public LoginResponseDto reissueTokens(String refreshToken) {
        Member member = memberService.findByKakaoRefreshToken(refreshToken);

        KakaoTokenResponseDto newToken = webClient.post()
                .uri(kakaoAuthUtil.getKakaoTokenReissueUrl(refreshToken))
                .retrieve()
                .bodyToMono(KakaoTokenResponseDto.class)
                .block();

        member.updateAccessToken(newToken.accessToken());
        if (newToken.refreshToken() != null) {
            member.updateRefreshToken(newToken.refreshToken());
        }

        return LoginResponseDto.of(newToken.accessToken(), member.getId(), newToken.refreshToken());

    }
}
