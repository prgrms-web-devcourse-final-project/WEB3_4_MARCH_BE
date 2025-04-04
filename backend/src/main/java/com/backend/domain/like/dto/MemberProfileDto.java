package com.backend.domain.like.dto;

import com.backend.domain.image.entity.Image;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberProfileDto {
    private Long id;
    private String nickname;
    private Image profileImage;
}
