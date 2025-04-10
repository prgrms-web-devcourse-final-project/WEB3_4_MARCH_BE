package com.backend.domain.userrecommendation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RecommendedUserDto {

    private Long id;
    private String nickname;
    private Double latitude;
    private Double longitude;
}
