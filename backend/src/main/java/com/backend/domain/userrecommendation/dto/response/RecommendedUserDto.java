package com.backend.domain.userrecommendation.dto.response;

import com.backend.domain.image.dto.ImageResponseDto;
import com.backend.domain.userkeyword.dto.response.UserKeywordResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RecommendedUserDto {

    private Long id;
    private String nickname;
    private Double latitude;
    private Double longitude;
    private Integer age;
    private String introduction;
    private List<UserKeywordResponse> keywords;
    private List<ImageResponseDto> images;

}
