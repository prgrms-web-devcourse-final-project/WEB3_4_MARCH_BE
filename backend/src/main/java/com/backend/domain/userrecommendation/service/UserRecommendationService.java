package com.backend.domain.userrecommendation.service;

import com.backend.domain.member.entity.Member;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.domain.userrecommendation.dto.response.RecommendedUserDto;
import com.backend.domain.userrecommendation.entity.UserRecommendation;
import com.backend.domain.userrecommendation.repository.UserRecommendationRepository;
import com.backend.global.redis.service.RedisGeoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserRecommendationService {

    private final RedisGeoService redisGeoService;
    private final MemberRepository memberRepository;
    private final UserRecommendationRepository userRecommendationRepository;
    private final BlockUserRepository blockUserRepository;

    @Transactional
    public List<RecommendedUserDto> generateRecommendations(Member me) {

        // 차단 유저 ID 목록 조회
        List<Long> blockedUserIds = blockUserRepository.findBlockedUserIds(me);

        // 이미 추천받았던 유저 ID 목록 조회
        Set<Long> recommendedIds = userRecommendationRepository.findAllByReceivingUser(me);

        // Redis에서 10km 반경 내 전체 사용자 ID 목록 조회
        List<Long> geoIds = redisGeoService.findNearByUserIds(me.getLatitude(), me.getLongitude(), 10.0, Integer.MAX_VALUE);

        // DB에서 유저 정보 조회
        List<Member> candidates = memberRepository.findAllById(geoIds);

        // 조건 필터링
        List<Member> filtered = candidates.stream()
                .filter(u -> !u.getId().equals(me.getId()))
                .filter(u -> !blockedUserIds.contains(u.getId()))
                .filter(u -> !recommendedIds.contains(u.getId()))
                .collect(Collectors.toList());

        Collections.shuffle(filtered);

        List<Member> selected = filtered.stream()
                .limit(10)
                .toList();

        List<UserRecommendation> records = selected.stream()
                .map(user -> UserRecommendation.builder()
                        .receivingUser(me)
                        .recommendedUser(user)
                        .recommendedDate(LocalDateTime.now())
                        .build())
                .toList();
        userRecommendationRepository.saveAll(records);

        return selected.stream()
                .map(user -> RecommendedUserDto.builder()
                        .id(user.getId())
                        .nickname(user.getNickname())
                        .latitude(user.getLatitude())
                        .longitude(user.getLongitude())
                        .build())
                .toList();


    }


}