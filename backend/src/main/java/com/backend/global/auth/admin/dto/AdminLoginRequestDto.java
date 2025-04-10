package com.backend.global.auth.admin.dto;

/**
 * 관리자 로그인 요청 정보를 담는 DTO
 */

public record AdminLoginRequestDto(
        String email,
        String password
) {}