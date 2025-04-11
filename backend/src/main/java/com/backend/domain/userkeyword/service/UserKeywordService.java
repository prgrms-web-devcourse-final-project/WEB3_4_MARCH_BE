package com.backend.domain.userkeyword.service;

import com.backend.domain.keyword.entity.Keyword;
import com.backend.domain.keyword.entity.KeywordCategory;
import com.backend.domain.keyword.repository.KeywordRepository;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.domain.userkeyword.dto.response.UserKeywordResponse;
import com.backend.domain.userkeyword.entity.UserKeyword;
import com.backend.domain.userkeyword.repository.UserKeywordRepository;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserKeywordService {

    private final UserKeywordRepository userKeywordRepository;
    private final KeywordRepository keywordRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void saveUserKeywords(Long userId, List<Long> keywordIds) {
        Member loginUser = memberRepository.findById(userId).orElseThrow(
                () -> new GlobalException(GlobalErrorCode.MEMBER_NOT_FOUND)
        );

        List<Keyword> keywords = keywordRepository.findAllById(keywordIds);

        for (Keyword keyword : keywords) {
            KeywordCategory category = keyword.getCategory();
            if (!category.isMultipleChoice()) {
                userKeywordRepository.deleteByMemberIdAndKeywordCategoryId(userId, category.getId());
            }
        }

        for (Keyword keyword : keywords) {
            UserKeyword userKeyword = UserKeyword.builder()
                    .member(loginUser)
                    .keyword(keyword)
                    .build();

            userKeywordRepository.save(userKeyword);
        }
    }

    // 회원정보 수정(업데이트)에서 사용하는 메서드
    @Transactional
    public void updateUserKeywords(Long userId, List<Long> keywordIds) {
        Member member = memberRepository.findById(userId).orElseThrow(
                () -> new GlobalException(GlobalErrorCode.MEMBER_NOT_FOUND)
        );

        userKeywordRepository.deleteAllKeywordsByMemberId(userId);

        List<Keyword> keywords = keywordRepository.findAllById(keywordIds);
        for (Keyword keyword : keywords) {
            UserKeyword userKeyword = UserKeyword.builder()
                    .member(member)
                    .keyword(keyword)
                    .build();
            userKeywordRepository.save(userKeyword);
        }
    }

    @Transactional(readOnly = true)
    public List<UserKeywordResponse> getUserKeywords(Long userId) {
        return userKeywordRepository.findAllByMemberId(userId).stream()
                .map(UserKeyword :: getKeyword)
                .map(UserKeywordResponse :: from)
                .toList();
    }


}
