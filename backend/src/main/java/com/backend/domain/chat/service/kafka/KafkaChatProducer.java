package com.backend.domain.chat.service.kafka;

import com.backend.domain.chat.dto.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka를 통해 채팅 메시지를 발행(Publish)하는 클래스입니다.
 * - Kafka의 "chat-massage" 토픽에 ChatMessage 객체를 전송합니다.
 */
@Component
@RequiredArgsConstructor
public class KafkaChatProducer {

    private final KafkaTemplate<String, ChatMessage> kafkaTemplate;
    private final ObjectMapper objectMapper;

    // 카프카 토픽명
    private static final String TOPIC_NAME = "chat-massage";

    /**
     * Kafka 토픽에 채팅 메시지를 전송합니다.
     *
     * @param message 전송할 채팅 메시지 DTO
     */
    public void sendMessage(ChatMessage message) {
        kafkaTemplate.send(TOPIC_NAME, message);
    }
}
