package com.backend.global.auth.admin.service;

import com.backend.domain.member.entity.Member;
import com.backend.domain.member.entity.Role;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.global.auth.admin.dto.AdminLoginRequestDto;
import com.backend.global.auth.admin.dto.AdminLoginResponseDto;
import com.backend.global.auth.kakao.util.TokenProvider;
import org.springframework.stereotype.Service;

@Service
public class AdminAuthService {

    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;

    public AdminAuthService(MemberRepository memberRepository,
                            TokenProvider tokenProvider) {
        this.memberRepository = memberRepository;
        this.tokenProvider = tokenProvider;
    }

    public AdminLoginResponseDto processAdminLogin(AdminLoginRequestDto request) {
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("관리자 계정이 존재하지 않습니다."));

        // 관리자로 사용하기 위해서는 미리 설정된 화이트리스트 등에 의해 회원의 역할이 ROLE_ADMIN으로 변경되어 있어야 합니다.
        if (!member.getRole().equals(Role.ROLE_ADMIN)) {
            throw new IllegalArgumentException("관리자 계정이 아닙니다.");
        }

        String token = tokenProvider.createAccessToken(member.getId(), member.getRole().name());
        return new AdminLoginResponseDto(token, member.getId());
    }
}
