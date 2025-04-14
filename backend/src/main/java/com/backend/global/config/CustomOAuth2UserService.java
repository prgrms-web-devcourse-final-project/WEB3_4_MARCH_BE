package com.backend.global.config;

import com.backend.domain.member.entity.Member;
import com.backend.domain.member.service.MemberService;
import com.backend.global.auth.kakao.service.CookieService;
import com.backend.global.auth.kakao.service.RedisRefreshTokenService;
import com.backend.global.auth.kakao.util.TokenProvider;
import com.backend.global.auth.model.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final TokenProvider tokenProvider;
    private final RedisRefreshTokenService redisRefreshTokenService;
    private final MemberService memberService;
    private final CookieService cookieService;
    private final HttpServletRequest httpServletRequest;
    private final HttpServletResponse httpServletResponse;

    // 소셜 로그인이 성공할 때마다 이 함수가 실행된다.
    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String oauthId = oAuth2User.getName();
        String providerTypeCode = userRequest
                .getClientRegistration()
                .getRegistrationId()
                .toUpperCase(Locale.getDefault());

        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, String> kakaoAccount = (Map<String, String>) attributes.get("kakao_account");
        Map<String, String> attributesProperties = (Map<String, String>) attributes.get("properties");

        String email = kakaoAccount.get("email");
        String nickname = attributesProperties.get("nickname");

        Member member = memberService.modifyMember(Long.parseLong(oauthId), email, nickname);

        // 4. JWT access, refresh 토큰 생성
        // JWT 토큰 발급 시 권한은 member.getRole() 기준으로 생성
        String accessToken = tokenProvider.createAccessToken(member.getId(), member.getRole().name());
        String refreshToken = tokenProvider.createRefreshToken(member.getId());
        long ttl = tokenProvider.getRefreshTokenExpiration();

        // 5. refreshToken 내 jti 추출
        String jti = tokenProvider.parseToken(refreshToken).getId();
        String ip = httpServletRequest.getRemoteAddr();
        String userAgent = httpServletRequest.getHeader("User-Agent");

        // 6. Redis에 리프레시 토큰 저장 (중복 로그인 방지)
        // Redis에 jti를 포함해 refreshToken과 클라이언트 정보 저장
        redisRefreshTokenService.saveRefreshToken(member.getId(), jti, refreshToken, ttl, ip, userAgent);

        cookieService.addAccessTokenToCookie(accessToken, httpServletResponse);
        cookieService.addRefreshTokenToCookie(refreshToken, httpServletResponse);

        return new CustomUserDetails(
                member.getId(),
                member.getEmail(),
                List.of(new SimpleGrantedAuthority(member.getRole().name()))
        );
    }
}