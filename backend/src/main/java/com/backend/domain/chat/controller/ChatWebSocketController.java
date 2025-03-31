package com.backend.domain.chat.controller;

import com.backend.domain.chat.dto.ChatMessage;
import com.backend.domain.chat.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;

    @MessageMapping("api/chat/send")
    public void send(ChatMessage message) {
        chatService.relayMessage(message);
    }
}
