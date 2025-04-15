package com.backend.domain.keyword.repository;

import com.backend.domain.keyword.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    List<Keyword> findAllByCategoryId(Long categoryId);

    @Query("SELECT k FROM Keyword k JOIN FETCH k.category WHERE k.id IN :ids")
    List<Keyword> findAllWithCategoryByIdIn(@Param("ids") List<Long> ids);
}