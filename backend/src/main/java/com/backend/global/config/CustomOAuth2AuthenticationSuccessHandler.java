package com.backend.global.config;

import com.backend.domain.member.service.MemberService;
import com.backend.global.auth.kakao.util.TokenProvider;
import com.backend.global.auth.model.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomOAuth2AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private final MemberService memberService;
    private final TokenProvider tokenProvider;

    @SneakyThrows
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String redirectUrl = request.getParameter("state");

        // 임시 사용자 여부 판단
        if (getActorAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_TEMP_USER"))) {
            redirectUrl += redirectUrl.contains("?") ? "&" : "?";
            redirectUrl += "needSignup";
        }

        // ✅ 액세스 토큰/리프레시 토큰 생성
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String accessToken = tokenProvider.createAccessToken(
                userDetails.getMemberId(),
                userDetails.getRole().name()
        );
        String refreshToken = tokenProvider.createRefreshToken(userDetails.getMemberId());

        // ✅ Set-Cookie 헤더 설정
        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .domain(".connect-to.shop") // ✅ 여기!
                .path("/")
                .maxAge(Duration.ofHours(6))
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .domain(".connect-to.shop")
                .path("/")
                .maxAge(Duration.ofDays(60))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        response.sendRedirect(redirectUrl);
    }


    public List<GrantedAuthority> getActorAuthorities() {
        return (List<GrantedAuthority>) Optional.ofNullable(
                        SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                )
                .map(Authentication::getPrincipal)
                .filter(principal -> principal instanceof CustomUserDetails)
                .map(principal -> (CustomUserDetails) principal)
                .map(customUserDetails -> customUserDetails.getAuthorities())
                .orElse(List.of());
    }
}