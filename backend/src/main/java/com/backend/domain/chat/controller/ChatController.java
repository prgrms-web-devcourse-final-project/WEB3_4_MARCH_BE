package com.backend.domain.chat.controller;

import com.backend.domain.chat.dto.request.ChatMessageRequest;
import com.backend.domain.chat.dto.response.ChatMessageResponse;
import com.backend.domain.chat.service.chat.ChatService;
import com.backend.domain.chatroom.dto.response.ChatRoomResponse;
import com.backend.domain.chatroom.service.ChatRoomService;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import com.backend.global.response.GenericResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 채팅 메시지 관련 컨트롤러입니다.
 * - 채팅방 내 메시지 조회 (REST API)
 * - 채팅 메시지 전송 (WebSocket 메시지 수신)
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("api/chat/chatrooms")
public class ChatController {

    private final ChatRoomService chatRoomService;
    private final ChatService chatService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    // 테스트용 하드코딩 sender
    private final MemberRepository memberRepository;

    /**
     * 특정 채팅방의 메시지를 조회합니다.
     * - 추후 안 읽은 메시지 수 포함 및 페이징 기능 추가 예정
     *
     * @param roomId 조회할 채팅방 ID
     * @return 해당 채팅방의 메시지 목록
     */
    @GetMapping("/{roomId}")
    public ResponseEntity<GenericResponse<List<ChatMessageResponse>>> getMessage(
            @PathVariable Long roomId) {
        List<ChatMessageResponse> messages = chatService.getRoomMessage(roomId);
        return ResponseEntity.ok(GenericResponse.ok(messages, "채팅방 메시지 조회 + 안 읽은 수 조회 성공"));
    }

    /**
     * WebSocket을 통해 수신된 채팅 메시지를 처리합니다.
     * - 현재는 sender를 MemberRepository에서 하드코딩으로 가져옵니다.
     * - 추후 JWT 인증을 통해 로그인 사용자 정보를 주입받도록 개선해야 합니다.
     *
     * @param chatMessageRequest 클라이언트로부터 수신한 메시지 요청 DTO
     */
    @MessageMapping("/{roomId}/message")
    public void sendMessage(ChatMessageRequest chatMessageRequest) {
        Member sender = memberRepository.findById(1L)
                        .orElseThrow(() -> new GlobalException(GlobalErrorCode.NOT_FOUND_MEMBER));

        chatService.sendMessage(chatMessageRequest, sender);
    }
}
