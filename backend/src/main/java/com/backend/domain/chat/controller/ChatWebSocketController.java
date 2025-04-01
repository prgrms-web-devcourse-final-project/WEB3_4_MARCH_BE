package com.backend.domain.chat.controller;

import com.backend.domain.chat.dto.ChatMessage;
import com.backend.domain.chat.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

/**
 * WebSocket을 통해 서버로 전달된 메시지를 처리하는 컨트롤러입니다.
 * - 클라이언트로부터 수신한 ChatMessage를 Kafka에서 수신한 것처럼 relay 처리합니다.
 * - 일반적으로 Kafka를 통해 들어오는 메시지는 consumer에서 처리되지만,
 *   테스트 시 WebSocket 직접 전송을 위한 entry point 역할을 합니다.
 */
@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;

    /**
     * WebSocket을 통해 클라이언트로부터 전달된 채팅 메시지를 처리합니다.
     * - ChatMessage는 Kafka를 통해 들어오는 메시지 형식과 동일합니다.
     * - 내부적으로 relayMessage()를 호출하여 DB 저장 및 Redis 전송까지 수행합니다.
     *
     * @param message 클라이언트로부터 수신한 ChatMessage DTO
     */
    @MessageMapping("chat/send")
    public void send(ChatMessage message) {
        chatService.relayMessage(message);
    }
}
