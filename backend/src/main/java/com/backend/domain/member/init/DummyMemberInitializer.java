//package com.backend.domain.member.init;
//
//import com.backend.domain.member.entity.Member;
//import com.backend.domain.member.repository.MemberRepository;
//import com.backend.global.redis.service.RedisGeoService;
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//
//
///**
// * 회원추천기능 테스트용 멤버 및 redis정보 주입 데이터
// *
// */
//@Component
//@RequiredArgsConstructor
//public class DummyMemberInitializer {
//
//    private final MemberRepository memberRepository;
//    private final RedisGeoService redisGeoService;
//
//    @PostConstruct
//    public void init() {
//
//        double centerLat = 37.5665;
//        double centerLon = 126.978;
//
//        // 반경 10km 이내 30명
//        for (int i = 0; i < 30; i++) {
//            double lat = centerLat + (Math.random() - 0.5) * 0.09; // 약 ±5km
//            double lon = centerLon + (Math.random() - 0.5) * 0.09;
//
//            Member member = memberRepository.save(
//                    Member.builder()
//                            .kakaoId(10000L + i)
//                            .nickname("near_user" + i)
//                            .age(20 + (i % 10))
//                            .height(160 + (i % 20))
//                            .gender(i % 2 == 0 ? "MALE" : "FEMALE")
//                            .latitude(lat)
//                            .longitude(lon)
//                            .chatAble(true)
//                            .build()
//            );
//
//            redisGeoService.addLocation(member.getId(), lat, lon);
//            System.out.println("[10km 이내] 저장됨: " + member.getNickname());
//        }
//
//        // 반경 10km 밖 (대략 20km ~ 40km 거리) 20명
//        for (int i = 0; i < 20; i++) {
//            double lat = centerLat + (Math.random() > 0.5 ? 0.3 : -0.3) + (Math.random() * 0.05);  // ±30km ~ 35km
//            double lon = centerLon + (Math.random() > 0.5 ? 0.3 : -0.3) + (Math.random() * 0.05);
//
//            Member member = memberRepository.save(
//                    Member.builder()
//                            .kakaoId(20000L + i)
//                            .nickname("far_user" + i)
//                            .age(20 + (i % 10))
//                            .height(160 + (i % 20))
//                            .gender(i % 2 == 0 ? "MALE" : "FEMALE")
//                            .latitude(lat)
//                            .longitude(lon)
//                            .chatAble(true)
//                            .build()
//            );
//
//            redisGeoService.addLocation(member.getId(), lat, lon);
//            System.out.println("[10km 밖] 저장됨: " + member.getNickname());
//        }
//    }
//
//
//}
