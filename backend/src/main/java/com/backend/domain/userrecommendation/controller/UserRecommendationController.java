package com.backend.domain.userrecommendation.controller;

import com.backend.domain.member.entity.Member;
import com.backend.domain.userrecommendation.dto.response.RecommendedUserDto;
import com.backend.domain.userrecommendation.service.UserRecommendationService;
import com.backend.global.auth.model.CustomUserDetails;
import com.backend.global.response.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseEntity<GenericResponse<List<RecommendedUserDto>>> dailyRecommend(
            @AuthenticationPrincipal CustomUserDetails loginUser
    ) {

        Member me = userRecommendationService.returnMember(loginUser);
        List<RecommendedUserDto> list = userRecommendationService.generateRecommendations(me);

        return ResponseEntity.ok(GenericResponse.ok(list, "사용자 추천이 성공되었습니다."));
    }

    @GetMapping
    public ResponseEntity<GenericResponse<List<RecommendedUserDto>>> getRecommendation(
            @AuthenticationPrincipal CustomUserDetails loginUser
    ) {
        Member me = userRecommendationService.returnMember(loginUser);
        List<RecommendedUserDto> recommendedUsers = userRecommendationService.getRecommendedUsers(me);

        return ResponseEntity.ok(GenericResponse.ok(recommendedUsers,"추천 유저 조회 성공"));
    }



}