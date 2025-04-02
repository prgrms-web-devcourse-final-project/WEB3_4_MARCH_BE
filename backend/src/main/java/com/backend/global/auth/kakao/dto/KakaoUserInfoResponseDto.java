package com.backend.global.auth.kakao.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 카카오 사용자 정보 API 응답을 매핑하는 DTO
 * 닉네임, 프로필 이미지, 이메일 등을 포함
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoUserInfoResponseDto(
        @JsonProperty("id") Long id,
        @JsonProperty("properties") Properties properties,
        @JsonProperty("kakao_account") KakaoAccount kakaoAccount
) {
    public record Properties(String nickname, String profile_image) {}
    public record KakaoAccount(String email) {}
}
