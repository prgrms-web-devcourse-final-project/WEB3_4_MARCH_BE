package com.backend.domain.blockuser.controller;

import com.backend.domain.blockuser.dto.request.BlockUserRequest;
import com.backend.domain.blockuser.dto.response.BlockedUserResponse;
import com.backend.domain.blockuser.service.BlockUserService;
import com.backend.global.auth.model.CustomUserDetails;
import com.backend.global.response.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BlockUserController {

    private final BlockUserService blockUserService;

    /**
     * 유저를 차단하는 버튼을 누를시 호출될 api
     *
     * @param request 차단할 회원
     */
    @PostMapping("/block-user")
    public GenericResponse<Void> blockUser(
            @AuthenticationPrincipal CustomUserDetails loginUser,
            @RequestBody BlockUserRequest request
    ) {

        blockUserService.blockUser(loginUser, request.getBlockedId());
        return GenericResponse.ok("회원 차단에 성공했습니다");
    }

    /**
     * 차단 해제 버튼을 누를시 호출될 api
     *
     * @param loginUser 로그인한 회원
     * @param request 차단된 회원
     */
    @DeleteMapping("/unblock-user")
    public GenericResponse<Void> unblockUser(
            @AuthenticationPrincipal CustomUserDetails loginUser,
            @RequestBody BlockUserRequest request
    ) {
        blockUserService.unblockUser(loginUser, request.getBlockedId());

        return GenericResponse.ok("회원 차단해제에 성공했습니다.");
    }

    /**
     * 내가 차단한 회원들 목록을 보여주는 api
     *
     * @param loginUser
     * @return List<BlockedUserResponse>
     */
    @GetMapping("/block-user")
    public GenericResponse<List<BlockedUserResponse>> blockedList(
            @AuthenticationPrincipal CustomUserDetails loginUser
    ) {
        List<BlockedUserResponse> blockedUsers = blockUserService.getBlockedUsers(loginUser);

        return GenericResponse.ok(blockedUsers, "차단회원 목록 조회 성공.");
    }







}
