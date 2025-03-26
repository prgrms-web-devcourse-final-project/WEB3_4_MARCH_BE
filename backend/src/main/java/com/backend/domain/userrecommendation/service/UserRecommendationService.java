package com.backend.domain.userrecommendation.service;

import com.backend.domain.userrecommendation.dto.response.RecommendedUserDto;
import com.backend.domain.userrecommendation.entity.UserRecommendation;
import com.backend.domain.userrecommendation.repository.UserRecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserRecommendationService {

    private final MemberRepository memberRepository;
    private final UserKeywordRepository userKeywordRepository;
    private final UserRecommendationRepository userRecommendationRepository;
    private final BlockUserRepository blockUserRepository;

    @Transactional
    public List<RecommendedUserDto> generateRecommendations(Member me) {

        // 기존 추천 삭제 (오늘 날짜 기준)
        userRecommendationRepository.deleteByReceivingUser(me);

        // 위치 기반 10km 이내 유저 조회
        List<Member> nearbyUsers = memberRepository.findNearbyUsers(me.getLatitude(), me.getLongitude());

        // 내 키워드 ID 목록
        List<Long> myKeywordIds = userKeywordRepository.findByUser(me).stream()
                .map(k -> k.getKeyword().getId())
                .toList();

        // 차단 유저 ID 목록
        Set<Long> blockedUserIds = blockUserRepository.findByUser(me).stream()
                .map(b -> b.getBlockedUser().getId())
                .collect(Collectors.toSet());

        // 공통 키워드 개수 기준 필터링 및 정렬
        List<RecommendedUserDto> result = nearbyUsers.stream()
                // 본인 id 제외
                .filter(u -> !u.getId().equals(me.getId()))
                // 차단 id 제외
                .filter(u -> !blockedUserIds.contains(u.getId()))
                .map(u -> {
                    List<Long> otherKeywordIds = userKeywordRepository.findByUser(u).stream()
                            .map(k -> k.getKeyword().getId())
                            .toList();

                    long keywordCnt = otherKeywordIds.stream()
                            .filter(myKeywordIds::contains)
                            .count();

                    return new AbstractMap.SimpleEntry<>(u, keywordCnt);
                })
                // 공통 키워드가 1개 이상 있는 경우만 필터링
                .filter(e -> e.getValue() > 0)
                // 공통 키워드 수 기준으로 내림차순 정렬
                .sorted((a,b) -> Long.compare(b.getValue(), a.getValue()))
                // 최대 10명까지만 추천
                .limit(10)
                .map(e -> {
                    Member user = e.getKey();

                    userRecommendationRepository.save(
                            UserRecommendation.builder()
                                    .receivingUser(me)
                                    .recommendedUser(user)
                                    .recommendedDate(LocalDateTime.now())
                                    .build()
                    );

                    return RecommendedUserDto.builder()
                            .id(user.getId())
                            .nickname(user.getNickname())
                            .latitude(user.getLatitude())
                            .longitude(user.getLongitude())
                            .build();
                })
                .toList();

        return result;
    }


}
