package com.backend.domain.userrecommendation.service;

import com.backend.domain.blockuser.repository.BlockUserRepository;
import com.backend.domain.image.dto.ImageResponseDto;
import com.backend.domain.image.service.ImageService;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.domain.userkeyword.dto.response.UserKeywordResponse;
import com.backend.domain.userkeyword.service.UserKeywordService;
import com.backend.domain.userrecommendation.dto.response.RecommendedUserDto;
import com.backend.domain.userrecommendation.entity.UserRecommendation;
import com.backend.domain.userrecommendation.repository.UserRecommendationRepository;
import com.backend.global.auth.model.CustomUserDetails;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
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
    private final UserKeywordService userKeywordService;
    private final ImageService imageService;

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

        if (selected.isEmpty()) {
            throw new GlobalException(GlobalErrorCode.NO_RECOMMENDATION_USER);
        }

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
                        .age(user.getAge())
                        .introduction(user.getIntroduction())
                        .keywords(userKeywordService.getUserKeywords(user.getId()))
                        .images(imageService.getImagesForMember(user.getId()))
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RecommendedUserDto> getRecommendedUsers(Member me) {

        LocalDateTime startOfToday = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfToday = startOfToday.plusDays(1);

        List<UserRecommendation> todayRecommendations =
                userRecommendationRepository
                        .findByReceivingUserAndRecommendedDateBetween(me, startOfToday, endOfToday);


        return todayRecommendations.stream()
                .map(record -> {
                    Member user = record.getRecommendedUser();

                    List<UserKeywordResponse> keywords = userKeywordService.getUserKeywords(user.getId());
                    List<ImageResponseDto> images = imageService.getImagesForMember(user.getId());


                    return RecommendedUserDto.builder()
                            .id(user.getId())
                            .nickname(user.getNickname())
                            .latitude(user.getLatitude())
                            .longitude(user.getLongitude())
                            .age(user.getAge())
                            .introduction(user.getIntroduction())
                            .keywords(keywords)
                            .images(images)
                            .build();
                })
                .toList();
    }

    public Member returnMember(CustomUserDetails loginUser) {
        return memberRepository.findById(loginUser.getMemberId()).orElseThrow(
                () -> new GlobalException(GlobalErrorCode.MEMBER_NOT_FOUND)
        );
    }


}