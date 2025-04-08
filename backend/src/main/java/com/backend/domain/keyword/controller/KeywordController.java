package com.backend.domain.keyword.controller;

import com.backend.domain.keyword.dto.response.KeywordCategoryResponse;
import com.backend.domain.keyword.service.KeywordService;
import com.backend.global.response.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/keywords")
public class KeywordController {

    private final KeywordService keywordService;

    /**
     * 모든 키워드 카테고리 및 해당 키워드 목록을 조회합니다.
     *
     *
     * @return 키워드 카테고리 및 키워드 리스트를 포함한 응답
     */
    @GetMapping
    public ResponseEntity<GenericResponse<List<KeywordCategoryResponse>>> getAllKeywords() {
        List<KeywordCategoryResponse> allKeywordCategories = keywordService.getAllKeywordCategories();

        return ResponseEntity.ok(GenericResponse.ok(allKeywordCategories));
    }


}
