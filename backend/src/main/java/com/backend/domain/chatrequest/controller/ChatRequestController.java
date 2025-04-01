package com.backend.domain.chatrequest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat-request")
public class ChatRequestController {

//    private final ChatRequestService chatRequestService;
//
//    /**
//     * 채팅요청을 보낼시 호출될 api
//     *
//     * @param loginUser 로그인된 사용자 정보 (요청자)
//     * @param receiverId 채팅 요청을 받을 사용자 id
//     * @return 처리 결과 메시지
//     */
//    @PostMapping("/{receiverId}")
//    public GenericResponse<Void> sendRequest(
//            @AuthenticationPrincipal CustomUserDetails loginUser,
//            @PathVariable("receiverId") Long receiverId
//            ) {
//        chatRequestService.sendChatRequest(loginUser, receiverId);
//
//        return GenericResponse.ok("채팅요청을 보냈습니다.");
//    }
//
//    /**
//     * 클라이언트에서 수락/거절 을 누를시 호출될 api
//     *
//     * @param requestId 처리할 ChatRequest ID
//     * @param accept true면 수락, false면 거절
//     * @return 처리 결과 메시지
//     */
//    @PostMapping("/respond/{requestId}")
//    public GenericResponse<Void> respond(
//            @PathVariable("requestId") Long requestId,
//            @RequestParam boolean accept
//    ) {
//        chatRequestService.respondToRequest(requestId, accept);
//
//        return GenericResponse.ok("요청 처리 완료.");
//    }
//
//    /**
//     *
//     * @param loginUser 로그인된 사용자 정보
//     * @return
//     */
//    @GetMapping("/sent-list")
//    public GenericResponse<List<ChatRequestDto>> getSentRequests(
//            @AuthenticationPrincipal CustomUserDetails loginUser
//    ) {
//        List<ChatRequestDto> list =  chatRequestService.getSentRequests(loginUser);
//
//        return GenericResponse.ok(list, "보낸 요청 목록 조회 성공");
//    }
//
//    /**
//     *
//     * @param loginUser 로그인된 사용자 정보
//     * @return
//     */
//    @GetMapping("/received-list")
//    public GenericResponse<List<ChatRequestDto>> getReceivedRequests(
//            @AuthenticationPrincipal CustomUserDetails loginUser
//    ) {
//        List<ChatRequestDto> list = chatRequestService.getReceivedRequests(loginUser);
//
//        return GenericResponse.of(list, "받은 요청 목록 조회 성공");
//    }


}
