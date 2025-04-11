package com.backend.domain.member.dto;

import com.backend.domain.image.dto.ImageResponseDto;
import com.backend.domain.member.entity.Member;

import java.util.List;
import java.util.stream.Collectors;

/**
 * API 응답 전용 DTO
 * 회원 정보 반환용 DTO (Controller → Client)
 */

public record MemberResponseDto(
        Long id,
        String nickname,
        String gender,
        Integer age,
        Integer height,
        ImageResponseDto profileImage,
        List<ImageResponseDto> images,
        String introduction,
        List<String> keywords,
        Boolean liked,
        Boolean chatRequest,
        Boolean blockStatus,
        Double latitude,
        Double longitude
) {
    public static MemberResponseDto from(Member member) {
        return new MemberResponseDto(
                member.getId(),
                member.getNickname(),
                member.getGender(),
                member.getAge(),
                member.getHeight(),
                member.getProfileImage() != null ? ImageResponseDto.from(member.getProfileImage()) : null,
                member.getImages().stream()
                    .map(ImageResponseDto::from)
                    .collect(Collectors.toList()),
                member.getIntroduction(),
                member.getKeywords(),// 유저키워드 엔티티를 통해 보여짐
                false, // 좋아요 는 Likes 엔티티를 통해 별도 계산 (기본값 false)
                false, // 채팅요청 여부는 실제 채팅 요청 여부에 따라 동적으로 결정 (기본값 false)
                member.getBlockStatus(),
                member.getLatitude(),
                member.getLongitude()
        );
    }
}
