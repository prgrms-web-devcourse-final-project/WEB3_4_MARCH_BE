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
//        for (int i = 0; i <= 15; i++) {
//            Member member = memberRepository.save(
//                    Member.builder()
//                            .kakaoId(1000L + i)
//                            .nickname("user" + i)
//                            .age(25 + (i % 5))
//                            .height(160 + (i % 10))
//                            .gender(i % 2 == 0 ? "MALE" : "FEMALE")
//                            .latitude(37.56 + (i * 0.001))   // 서울 중심 기준
//                            .longitude(127.01 + (i * 0.001))
//                            .chatAble(true)
//                            .build()
//            );
//
//            System.out.println("[DB] Member 저장됨: " + member.getId());
//
//            redisGeoService.addLocation(member.getId(), member.getLatitude(), member.getLongitude());
//
//        }
//    }
//
//
//}
