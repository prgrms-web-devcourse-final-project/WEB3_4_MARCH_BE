package com.backend.domain.keyword.repository;

import com.backend.domain.keyword.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    Collection<Keyword> findAllByCategoryId(Long categoryId);
}



