package com.backend.domain.userkeyword.repository;

import com.backend.domain.userkeyword.entity.UserKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserKeywordRepository extends JpaRepository<UserKeyword, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM UserKeyword uk WHERE uk.member.id = :memberId AND uk.keyword.category.id = :categoryId")
    void deleteByMemberIdAndKeywordCategoryId(@Param("memberId") Long memberId, @Param("categoryId") Long categoryId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM UserKeyword uk WHERE uk.member.id = :memberId")
    void deleteAllKeywordsByMemberId(@Param("memberId") Long memberId); // 프로필의 키워드를 수정할 때 사용, memberId에 해당하는 모든 키워드를 삭제함

    List<UserKeyword> findAllByMemberId(Long memberId);


}
