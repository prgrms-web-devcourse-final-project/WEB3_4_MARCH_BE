//package com.backend.domain.userrecommendation.service;
//
//import com.backend.domain.blockuser.repository.BlockUserRepository;
//import com.backend.domain.image.service.ImageService;
//import com.backend.domain.member.entity.Member;
//import com.backend.domain.member.entity.MemberStatus;
//import com.backend.domain.member.entity.Role;
//import com.backend.domain.member.repository.MemberRepository;
//import com.backend.domain.userkeyword.service.UserKeywordService;
//import com.backend.domain.userrecommendation.repository.UserRecommendationRepository;
//import com.backend.global.redis.service.RedisGeoService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.util.List;
//
//public class UserRecommendationServiceTest {
//
//    @InjectMocks
//    private UserRecommendationService userRecommendationService;
//
//    @Mock private RedisGeoService redisGeoService;
//    @Mock private MemberRepository memberRepository;
//    @Mock private UserRecommendationRepository userRecommendationRepository;
//    @Mock private BlockUserRepository blockUserRepository;
//    @Mock private UserKeywordService userKeywordService;
//    @Mock private ImageService imageService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    // 더미 Member
//    private Member createMember(Long id, String nickname, double lat, double lon) {
//        return Member.builder()
//                .id(id)
//                .kakaoId(id + 1000)
//                .email(nickname + "@test.com")
//                .nickname(nickname)
//                .age(25)
//                .height(170)
//                .gender("MALE")
//                .latitude(lat)
//                .longitude(lon)
//                .chatAble(true)
//                .role(Role.ROLE_USER)
//                .status(MemberStatus.ACTIVE)
//                .isDeleted(false)
//                .build();
//    }
//
//    @Test
//    @DisplayName("조건에 맞는 유저가 2명일 때 추천")
//    void generateRecommendations_twoUsers() {
//        Member me = createMember(1L, "나", 37.5, 126.9);
//        List<Member> candidates = List.of(
//                createMember(2L, "유저2", 37.51, 126.91),
//                createMember(3L, "유저3", 37.52, 126.92)
//        );
//
//
//
//
//
//    }
//
//
//
//
//
//
//
//}
