package com.backend.global.auth.admin.dto;

public record AdminLoginResponseDto(
        String accessToken,
        Long adminId
) {}
