package com.backend.domain.userrecommendation.scheduler;

import com.backend.domain.member.entity.Member;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.domain.userrecommendation.service.UserRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RecommendationScheduler {

    private final MemberRepository memberRepository;
    private final UserRecommendationService userRecommendationService;

    @Scheduled(cron = "0 0 0 * * *")
    public void runDailyRecommendation() {
        List<Member> members = memberRepository.findAll();
        for (Member member : members) {
            userRecommendationService.generateRecommendations(member);
        }
    }

}
