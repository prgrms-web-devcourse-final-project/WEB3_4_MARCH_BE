package com.backend.domain.userrecommendation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class RecommendedUserDto {

    private long id;
    private String nickname;
    private Double latitude;
    private Double longitude;
}
