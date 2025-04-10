package com.backend.domain.chatrequest.controller;

import com.backend.domain.chatrequest.dto.response.ChatRequestDto;
import com.backend.domain.chatrequest.service.ChatRequestService;
import com.backend.global.auth.model.CustomUserDetails;
import com.backend.global.response.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat-request")
public class ChatRequestController {

    private final ChatRequestService chatRequestService;

    /**
     * 채팅요청을 보낼시 호출될 api
     *
     * @param loginUser 로그인된 사용자 정보 (요청자)
     * @param receiverId 채팅 요청을 받을 사용자 id
     * @return 처리 결과 메시지
     */
    @PostMapping("/{receiverId}")
    public ResponseEntity<GenericResponse<Void>> sendRequest(
            @AuthenticationPrincipal CustomUserDetails loginUser,
            @PathVariable("receiverId") Long receiverId
            ) {
        chatRequestService.sendChatRequest(loginUser, receiverId);

        return ResponseEntity.ok(GenericResponse.ok("채팅요청을 보냈습니다."));
    }

    /**
     * 클라이언트에서 수락/거절 을 누를시 호출될 api
     *
     * @param requestId 처리할 ChatRequest테이블 ID
     * @param accept true면 수락, false면 거절
     * @return 처리 결과 메시지
     */
    @PostMapping("/respond/{requestId}")
    public ResponseEntity<GenericResponse<Void>> respond(
            @PathVariable("requestId") Long requestId,
            @RequestParam boolean accept
    ) {
        chatRequestService.respondToRequest(requestId, accept);

        return ResponseEntity.ok(GenericResponse.ok("요청 처리 완료."));
    }

    /**
     * 내가 보낸 채팅요청 목록 조회 api
     *
     * @param loginUser 로그인된 사용자 정보
     * @return
     */
    @GetMapping("/sent-list")
    public ResponseEntity<GenericResponse<List<ChatRequestDto>>> getSentRequests(
            @AuthenticationPrincipal CustomUserDetails loginUser
    ) {
        List<ChatRequestDto> list =  chatRequestService.getSentRequests(loginUser);

        return ResponseEntity.ok(GenericResponse.ok(list, "보낸 요청 목록 조회 성공"));
    }

    /**
     * 내가 받은 채팅요청 목록 조회 api
     *
     * @param loginUser 로그인된 사용자 정보
     * @return
     */
    @GetMapping("/received-list")
    public ResponseEntity<GenericResponse<List<ChatRequestDto>>> getReceivedRequests(
            @AuthenticationPrincipal CustomUserDetails loginUser
    ) {
        List<ChatRequestDto> list = chatRequestService.getReceivedRequests(loginUser);

        return ResponseEntity.ok(GenericResponse.of(list, "받은 요청 목록 조회 성공"));
    }


}
