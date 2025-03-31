package com.backend.domain.member.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.backend.domain.image.dto.ImageResponseDto;
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
        ImageResponseDto profileImage,
        List<ImageResponseDto> images,
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
                member.getProfileImage() != null ? ImageResponseDto.from(member.getProfileImage()) : null,
                member.getImages().stream()
                    .map(ImageResponseDto::from)
                    .collect(Collectors.toList()),
                member.getLatitude(),
                member.getLongitude()
        );
    }
}
