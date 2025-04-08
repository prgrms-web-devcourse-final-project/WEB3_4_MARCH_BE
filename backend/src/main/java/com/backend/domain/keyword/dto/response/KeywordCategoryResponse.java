package com.backend.domain.keyword.dto.response;

import com.backend.domain.keyword.entity.KeywordCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KeywordCategoryResponse {

    private Long categoryId;
    private String categoryName;
    private boolean multipleChoice;
    private List<KeywordResponse> keywords;

    public static KeywordCategoryResponse from(KeywordCategory category, List<KeywordResponse> keywords) {
        return KeywordCategoryResponse.builder()
                .categoryId(category.getId())
                .categoryName(category.getCategoryName())
                .multipleChoice(category.isMultipleChoice())
                .keywords(keywords)
                .build();
    }


}
