package com.backend.global.auth.model;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Spring Security의 Authentication 객체 내부에 저장될 사용자 정보 클래스
 * 사용자 ID, 이메일, 권한 목록을 담고 있으며, 인증된 사용자 정보를 표현함
 */
public class CustomUserDetails implements UserDetails, OAuth2User {
    @Getter
    private final Long memberId;
    private final String email;
    private final List<GrantedAuthority> authorities;

    public CustomUserDetails(Long memberId, String email, List<GrantedAuthority> authorities) {
        this.memberId = memberId;
        this.email = email;
        this.authorities = authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Map.of();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    // 소셜 로그인에서는 사용하지 않음
    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return getUsername();
    }
}