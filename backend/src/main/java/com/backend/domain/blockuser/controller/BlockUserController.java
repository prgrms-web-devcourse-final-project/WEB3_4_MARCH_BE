package com.backend.domain.blockuser.controller;

import com.backend.domain.blockuser.dto.request.BlockUserRequest;
import com.backend.domain.blockuser.service.BlockUserService;
import com.backend.global.response.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/blocks")
public class BlockUserController {

    private final BlockUserService blockUserService;


    /**
     * 유저를 차단하는 버튼을 누를시 호출될 api
     *
     * @param request 요청 dto
     * @return
     */
    @PostMapping
    public GenericResponse<Void> blockUser(@RequestBody BlockUserRequest request) {
        blockUserService.blockUser(request.getBlockerId(), request.getBlockedId());

        return GenericResponse.ok("유저 차단에 성공했습니다");
    }

//    @DeleteMapping
//    public GenericResponse<Void> unblockUser()






}
