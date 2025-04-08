package com.backend.domain.keyword.repository;

import com.backend.domain.keyword.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    List<Keyword> findAllByCategoryId(Long categoryId);
}



