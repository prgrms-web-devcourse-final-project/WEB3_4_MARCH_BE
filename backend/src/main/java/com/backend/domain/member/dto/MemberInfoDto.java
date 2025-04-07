package com.backend.domain.member.dto;

import com.backend.domain.image.entity.Image;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.entity.Role;
import lombok.Builder;

import java.util.List;

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
        List<Image> images,
        Double latitude,
        Double longitude,
        Role role
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
                member.isChatAble(),
                member.getImages(),
                member.getLatitude(),
                member.getLongitude(),
                member.getRole()
        );
    }
}

