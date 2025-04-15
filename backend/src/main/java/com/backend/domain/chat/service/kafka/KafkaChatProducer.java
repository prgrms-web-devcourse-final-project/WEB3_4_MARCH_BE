package com.backend.domain.chat.service.kafka;

import com.backend.domain.chat.dto.ChatMessage;
import com.backend.global.config.KafkaTopicConfig;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
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

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    // 카프카 토픽명
    private static final String TOPIC_NAME = KafkaTopicConfig.CHAT_TOPIC;

    /**
     * Kafka 토픽에 채팅 메시지를 전송합니다.
     * ChatMessage 객체를 JSON 문자열로 변환하여 전송합니다.
     *
     * @param message 전송할 채팅 메시지 DTO
     */
    public void sendMessage(ChatMessage message) {

        try {
            // ChatMessage 객체를 JSON 문자열로 변환
            String jsonMessage = objectMapper.writeValueAsString(message);

            // Kafka 토픽에 JSON 문자열 전송
            kafkaTemplate.send(TOPIC_NAME, jsonMessage);
        } catch (Exception e) {
            throw new GlobalException(GlobalErrorCode.KAFKA_SEND_FAILURE);
        }
    }
}
