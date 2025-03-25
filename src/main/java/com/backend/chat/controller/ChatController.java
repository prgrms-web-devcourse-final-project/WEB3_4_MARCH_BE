package com.backend.chat.controller;

import com.backend.chat.dto.ChatMessage;
import com.backend.chat.service.KafkaChatProducer;
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
