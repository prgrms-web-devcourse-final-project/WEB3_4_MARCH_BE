package com.backend.domain.chat.controller;

import com.backend.domain.chat.dto.ChatMessage;
import com.backend.domain.chat.service.KafkaChatProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final KafkaChatProducer chatProducer;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(ChatMessage message) {
        chatProducer.sendMessage(message);
    }
}
