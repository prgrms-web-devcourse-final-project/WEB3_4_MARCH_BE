package com.backend.global.auth.kakao.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 카카오 토큰 응답 DTO
 * - 카카오 서버에서 받은 토큰 정보를 매핑하는 역할
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoTokenResponseDto(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("refresh_token") String refreshToken,
        @JsonProperty("expires_in") Long expiresIn
) {}
