package com.backend.global.config;

import com.backend.domain.member.service.MemberService;
import com.backend.global.auth.model.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomOAuth2AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private final MemberService memberService;

    @SneakyThrows
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String redirectUrl = request.getParameter("state");

        if ( getActorAuthorities()
                .stream()
                .anyMatch(
                        authority -> authority.getAuthority().equals("ROLE_TEMP_USER")
                ) ) {
            if ( redirectUrl.contains("?") ) {
                redirectUrl += "&";
            } else {
                redirectUrl += "?";
            }

            redirectUrl += "needSignup";
        }

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