package com.backend.domain.userkeyword.controller;

import com.backend.domain.userkeyword.dto.request.UserKeywordSaveRequest;
import com.backend.domain.userkeyword.dto.response.UserKeywordResponse;
import com.backend.domain.userkeyword.service.UserKeywordService;
import com.backend.global.auth.model.CustomUserDetails;
import com.backend.global.response.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-keywords")
public class UserKeywordController {

    private final UserKeywordService userKeywordService;


    /**
     * 사용자가 지정한 키워드를 저장합니다.
     *
     * @param loginUser 로그인 사용자 정보
     * @param request 사용자가 고른 키워드 Ids
     * @return
     */
    @PostMapping
    public ResponseEntity<GenericResponse<Void>> saveKeywords(
            @AuthenticationPrincipal CustomUserDetails loginUser,
            @RequestBody UserKeywordSaveRequest request
    ) {
        userKeywordService.saveUserKeywords(loginUser.getMemberId(), request.getKeywordIds());

        return ResponseEntity.ok(GenericResponse.ok("회원이 지정한 키워드를 저장했습니다."));
    }

    /**
     * 로그인된 사용자가 저장한 키워드 목록을 조회한다.
     *
     * @param loginUser 로그인 사용자 정보
     * @return 키워드 DTO 리스트
     */
    @GetMapping
    public ResponseEntity<GenericResponse<List<UserKeywordResponse>>> getMyKeywords(
            @AuthenticationPrincipal CustomUserDetails loginUser
    ) {
        List<UserKeywordResponse> userKeywords = userKeywordService.getUserKeywords(loginUser.getMemberId());

        return ResponseEntity.ok(GenericResponse.ok(userKeywords));
    }




}
