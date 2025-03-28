package com.backend.domain.chat.controller;

import com.backend.domain.chat.dto.request.ChatMessageRequest;
import com.backend.domain.chat.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;

    @MessageMapping("api/chat/send")
    public void send(ChatMessageRequest message) {
        chatService.relayMessage(message);
    }
}
