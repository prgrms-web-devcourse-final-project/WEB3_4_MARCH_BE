package com.backend.domain.chat.service.kafka;

import com.backend.domain.chat.dto.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaChatProducer {

    private final KafkaTemplate<String, ChatMessage> kafkaTemplate;
    private final ObjectMapper objectMapper;

    // 카프카 토픽명
    private static final String TOPIC_NAME = "chat-massage";

    public void sendMessage(ChatMessage message) {
        kafkaTemplate.send(TOPIC_NAME, message);
    }
}
