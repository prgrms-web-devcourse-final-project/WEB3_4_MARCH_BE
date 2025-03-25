package com.backend.chat.service;

import com.backend.chat.dto.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaChatConsumer {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "chat-messages", groupId = "chat-group")
    public void consume(String message) {
        try {
            ChatMessage chatMessage = objectMapper.readValue(message, ChatMessage.class);
            messagingTemplate.convertAndSend(
                    "/topic/chat-room-"
                            + chatMessage.getRoomId(), message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
