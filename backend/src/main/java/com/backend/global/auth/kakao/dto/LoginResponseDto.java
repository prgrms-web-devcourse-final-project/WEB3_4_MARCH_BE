package com.backend.global.auth.kakao.dto;

import lombok.Builder;

/**
 * 로그인 성공 시 클라이언트에 전달되는 응답 DTO
 * accessToken, refreshToken, memberId, isRegistered 포함
 */
@Builder
public record LoginResponseDto(
        String accessToken,
        String kakaoId,
        String memberId,
        String refreshToken,
        boolean isRegistered
) {

    /**
     * 응답 객체 생성 메서드
     *
     * @param accessToken  발급된 액세스 토큰
     * @param kakaoId      카카오 회원 ID (String으로 변환)
     * @param memberId     멤버 ID (PK)
     * @param refreshToken 발급된 리프레시 토큰
     * @param isRegistered 기존 회원 여부
     * @return 응답 DTO
     */
    public static LoginResponseDto of(String accessToken, Long kakaoId, Long memberId, String refreshToken, boolean isRegistered) {
        return new LoginResponseDto(
                accessToken,
                String.valueOf(kakaoId),
                memberId != null ? String.valueOf(memberId) : null,
                refreshToken,
                isRegistered
        );
    }
}

