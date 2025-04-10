package com.backend.global.auth.admin.dto;

public record AdminLoginRequestDto(
        String email,
        String password
) {}