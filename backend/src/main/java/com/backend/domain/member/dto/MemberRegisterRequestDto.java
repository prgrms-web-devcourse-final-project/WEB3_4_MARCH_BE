package com.backend.domain.member.dto;

import org.hibernate.validator.constraints.Length;

import com.backend.global.auth.kakao.dto.KakaoUserInfoResponseDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 회원 가입 요청 DTO (소셜 로그인 후 최초 등록 시)
 */
public record MemberRegisterRequestDto(

        @NotNull(message = "카카오 ID는 필수입니다.")
        Long kakaoId,

        @NotBlank(message = "이메일은 필수입니다.")
        String email,

        @NotBlank(message = "닉네임은 필수입니다.")
        @Length(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하입니다.")
        String nickname,

        @NotBlank(message = "성별은 필수입니다.")
        String gender,

        @NotNull(message = "나이를 입력해주세요.")
        Integer age,

        @NotNull(message = "키를 입력해주세요.")
        Integer height,

        // 위도, 경도 추가
        Double latitude,
        Double longitude,

        String introduction // 소개글
) {
        /**
         * 카카오 사용자 정보로부터 회원가입 DTO 생성
         */
        public static MemberRegisterRequestDto fromKakao(KakaoUserInfoResponseDto dto) {
                return new MemberRegisterRequestDto(
                        dto.id(),
                        dto.kakaoAccount().email(),
                        dto.properties().nickname(),
                        "UNKNOWN",     // 기본 성별
                        0,             // 기본 나이
                        0,             // 기본 키
//                        new ArrayList<>(), // 빈 프로필 이미지 리스트
                        null,          // 위도
                        null,           // 경도
                        null
                );
        }
}
