package com.backend.domain.keyword.service;

import com.backend.domain.keyword.dto.response.KeywordCategoryResponse;
import com.backend.domain.keyword.dto.response.KeywordResponse;
import com.backend.domain.keyword.repository.KeywordCategoryRepository;
import com.backend.domain.keyword.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KeywordService {

    private final KeywordRepository keywordRepository;
    private final KeywordCategoryRepository keywordCategoryRepository;


    public List<KeywordCategoryResponse> getAllKeywordCategories() {

        return keywordCategoryRepository.findAll().stream()
                .map(category -> {
                    List<KeywordResponse> keywords = keywordRepository.findAllByCategoryId(category.getId())
                            .stream()
                            .map(KeywordResponse :: from)
                            .toList();
                    return KeywordCategoryResponse.from(category, keywords);
                })
                .toList();
    }







}
