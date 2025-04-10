package com.backend.global.auth.admin.dto;

/**
 * 관리자 로그인 성공 시 반환되는 응답 DTO
 */

public record AdminLoginResponseDto(
        String accessToken, // 관리자용 JWT 토큰 (관리자용, 긴 TTL 및 isAdmin claim 포함됨)
        Long adminId // 관리자 계정의 고유 ID
) {}
