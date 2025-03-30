package com.backend.domain.member.dto;

import java.util.List;

import com.backend.domain.image.entity.Image;
import com.backend.domain.member.entity.Member;

/**
 * API 응답 전용 DTO
 * 회원 정보 반환용 DTO (Controller → Client)
 */

public record MemberResponseDto(
        Long id,
        String email,
        String nickname,
        String gender,
        Integer age,
        Integer height,
        List<Image> images,
        Double latitude,
        Double longitude
) {
    public static MemberResponseDto from(Member member) {
        return new MemberResponseDto(
                member.getId(),
                member.getEmail(),
                member.getNickname(),
                member.getGender(),
                member.getAge(),
                member.getHeight(),
                member.getImages(),
                member.getLatitude(),
                member.getLongitude()
        );
    }
}
