package com.backend.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;


/**
 * 사용자 정보 수정에 사용할 Dto
 */

@Builder
public record MemberModifyRequestDto(
        @NotBlank(message = "닉네임은 필수입니다.")
        @Length(min = 2, max = 10, message = "닉네임은 최소 2글자 이상, 10글자 이하이어야 합니다.")
        String nickname,

        @NotNull(message = "나이를 입력해주세요.")
        Integer age,

        @NotNull(message = "키를 입력해주세요.")
        Integer height,

        @NotBlank(message = "성별은 필수입니다.")
        String gender,

        // 위도, 경도 정보
        Double latitude,
        Double longitude,

        String introduction    // 수정한 소개글
) {
}
