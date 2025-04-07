package com.backend.domain.member.dto;

/**
 * 회원 가입 완료 후 클라이언트에 반환할 응답 DTO
 * - 회원 정보
 * - JWT accessToken
 * - JWT refreshToken 포함
 */

public record MemberRegisterResponseDto(
        MemberInfoDto member,
        String accessToken,
        String refreshToken
) {}
