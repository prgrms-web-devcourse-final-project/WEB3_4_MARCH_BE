package com.backend.domain.image.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ImageResponseDto {
    private Long id;
    private String url;
    private Boolean isPrimary;

    public static ImageResponseDto from(com.backend.domain.image.entity.Image image) {
        return new ImageResponseDto(
                image.getId(),
                image.getUrl(),
                image.getIsPrimary()
        );
    }
}
