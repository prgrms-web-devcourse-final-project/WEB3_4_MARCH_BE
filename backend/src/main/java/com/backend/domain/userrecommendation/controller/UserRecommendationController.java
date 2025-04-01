package com.backend.domain.userrecommendation.controller;

import com.backend.domain.member.entity.Member;
import com.backend.domain.userrecommendation.dto.response.RecommendedUserDto;
import com.backend.domain.userrecommendation.service.UserRecommendationService;
import com.backend.global.response.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/matching")
@RequiredArgsConstructor
public class UserRecommendationController {

    private final UserRecommendationService userRecommendationService;

    @PostMapping("/daily-recommend")
    public GenericResponse<List<RecommendedUserDto>> dailyRecommend(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        Member member = customUserDetails.getMember();
        List<RecommendedUserDto> list = userRecommendationService.generateRecommendations(member);

        return GenericResponse.ok(list, "사용자 추천이 성공되었습니다.");
    }



}
