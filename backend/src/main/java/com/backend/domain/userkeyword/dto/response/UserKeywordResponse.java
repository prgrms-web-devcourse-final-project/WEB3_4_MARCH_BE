package com.backend.domain.userkeyword.dto.response;

import com.backend.domain.keyword.entity.Keyword;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserKeywordResponse {

    private Long id;
    private String name;
    private Long categoryId;
    private String categoryName;

    public static UserKeywordResponse from(Keyword keyword) {
        return UserKeywordResponse.builder()
                .id(keyword.getId())
                .name(keyword.getName())
                .categoryId(keyword.getCategory() != null ? keyword.getCategory().getId() : null)
                .categoryName(keyword.getCategory() != null ? keyword.getCategory().getCategoryName() : null)
                .build();
    }
}
