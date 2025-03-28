package com.backend.domain.member.dto;

import com.backend.domain.member.entity.Member;
import lombok.Builder;

/**
 * 서비스 내부 조회/리스트 반환용 Dto (Service → API/Query)
 */

@Builder
public record MemberInfoDto(
        Long id,
        Long kakaoId,
        String email,
        String nickname,
        Integer age,
        Integer height,
        String gender,
        Boolean chatAble,
        String profileImage,
        Double latitude,
        Double longitude
) {
    public static MemberInfoDto from(Member member) {
        return MemberInfoDto.builder()
                .id(member.getId())
                .kakaoId(member.getKakaoId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .age(member.getAge())
                .height(member.getHeight())
                .gender(member.getGender())
                .chatAble(member.getChatAble())
                .profileImage(member.getProfileImage())
                .latitude(member.getLatitude())
                .longitude(member.getLongitude())
                .build();
    }
}

