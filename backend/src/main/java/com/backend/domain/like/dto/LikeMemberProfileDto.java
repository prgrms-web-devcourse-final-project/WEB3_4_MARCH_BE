package com.backend.domain.like.dto;

import com.backend.domain.image.dto.ProfileImageDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LikeMemberProfileDto {
    private Long id;
    private String nickname;
    private ProfileImageDto profileImage;
}
