package com.backend.domain.userkeyword.repository;

import com.backend.domain.userkeyword.entity.UserKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserKeywordRepository extends JpaRepository<UserKeyword, Long> {

    @Modifying
    @Query("DELETE FROM UserKeyword uk WHERE uk.member.id = :memberId AND uk.keyword.category.id = :categoryId")
    void deleteByMemberIdAndKeywordCategoryId(@Param("memberId") Long memberId, @Param("categoryId") Long categoryId);

    List<UserKeyword> findAllByMemberId(Long memberId);


}
