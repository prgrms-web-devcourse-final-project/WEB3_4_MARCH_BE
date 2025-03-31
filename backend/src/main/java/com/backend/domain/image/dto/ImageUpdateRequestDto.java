package com.backend.domain.image.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageUpdateRequestDto {
    @NotNull(message = "isPrimary 필드는 필수입니다.")
    private Boolean isPrimary;
}
