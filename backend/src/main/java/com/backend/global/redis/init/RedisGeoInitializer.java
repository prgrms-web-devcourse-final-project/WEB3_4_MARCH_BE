package com.backend.global.redis.init;


import com.backend.domain.member.entity.Member;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.global.redis.service.RedisGeoService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

// 운영 Redis GEO에 사용자의 정보를 하드코딩할 용도
@Component
@RequiredArgsConstructor
public class RedisGeoInitializer {

    private final MemberRepository memberRepository;
    private final RedisGeoService redisGeoService;

    @PostConstruct
    public void init() {
        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            redisGeoService.addLocation(member.getId(), member.getLatitude(), member.getLongitude());
            System.out.println("[Redis GEO] 등록됨: " + member.getId());
        }

    }


}
