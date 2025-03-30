package com.backend.domain.member.dto;

import java.util.List;

import com.backend.domain.image.entity.Image;
import com.backend.domain.member.entity.Member;

import lombok.Builder;

/**
 * 서비스 내부 전용 DTO로 설계
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
        Image profileImage,
        List<Image> images,
        Double latitude,
        Double longitude
) {
    public static MemberInfoDto from(Member member) {
        return new MemberInfoDto(
                member.getId(),
                member.getKakaoId(),
                member.getEmail(),
                member.getNickname(),
                member.getAge(),
                member.getHeight(),
                member.getGender(),
                member.getChatAble(),
                member.getProfileImage(),
                member.getImages(),
                member.getLatitude(),
                member.getLongitude()
        );
    }
}

