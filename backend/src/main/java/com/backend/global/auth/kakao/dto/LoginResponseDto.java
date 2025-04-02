package com.backend.global.auth.kakao.dto;

import lombok.Builder;

/**
 * 로그인 성공 시 클라이언트에 전달되는 응답 DTO
 * accessToken, userId, refreshToken을 포함
 */

@Builder
public record LoginResponseDto(String accessToken, String memberId, String refreshToken) {

    /**
     * 응답 객체 생성 메서드
     */
    public static LoginResponseDto of(String accessToken, Long memberId, String refreshToken) {
        return new LoginResponseDto(accessToken, String.valueOf(memberId), refreshToken);
    }
}

