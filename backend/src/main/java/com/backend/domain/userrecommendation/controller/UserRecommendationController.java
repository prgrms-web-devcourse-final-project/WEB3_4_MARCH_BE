package com.backend.domain.userrecommendation.controller;

import com.backend.domain.member.entity.Member;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.domain.userrecommendation.dto.response.RecommendedUserDto;
import com.backend.domain.userrecommendation.service.UserRecommendationService;
import com.backend.global.auth.model.CustomUserDetails;
import com.backend.global.response.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/matching")
@RequiredArgsConstructor
public class UserRecommendationController {

    private final UserRecommendationService userRecommendationService;

    private final MemberRepository memberRepository;


    @PostMapping("/daily-recommend")
    public GenericResponse<List<RecommendedUserDto>> dailyRecommend(
            @AuthenticationPrincipal CustomUserDetails loginUser
    ) {

        Member me = userRecommendationService.returnMember(loginUser);
        List<RecommendedUserDto> list = userRecommendationService.generateRecommendations(me);

        return GenericResponse.ok(list, "사용자 추천이 성공되었습니다.");
    }



}