package com.backend.domain.member.dto;

import com.backend.domain.image.dto.ImageResponseDto;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.entity.Role;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

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
        List<ImageResponseDto> images,
        Double latitude,
        Double longitude,
        Role role,
        String introduction
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
                member.getImages().stream()
                        .map(ImageResponseDto::from)
                        .collect(Collectors.toList()),
                member.getLatitude(),
                member.getLongitude(),
                member.getRole(),
                member.getIntroduction()
        );
    }
}

