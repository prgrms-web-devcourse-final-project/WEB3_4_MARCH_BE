package com.backend.domain.member.dto;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.backend.domain.image.entity.Image;

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

        @NotBlank(message = "프로필 이미지는 필수입니다.")
        List<Image> images,

        // 위도, 경도 추가
        Double latitude,
        Double longitude

) {}
