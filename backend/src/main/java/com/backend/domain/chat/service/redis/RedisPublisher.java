package com.backend.domain.chat.service.redis;

import com.backend.domain.chat.dto.ChatMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Redis Pub/Sub을 통해 채팅 메시지를 발행(Publish)하는 클래스입니다.
 * - 채팅 메시지를 JSON으로 직렬화한 후 Redis 채널에 발행합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RedisPublisher {
    private static final String CHANNEL = "chat-message";  // Redis Pub/Sub 채널명

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Redis 채널에 메시지를 발행합니다.
     *
     * @param message 전송할 채팅 메시지 DTO
     */
    public void publish(ChatMessage message) {
        try {
            String messageJson = objectMapper.writeValueAsString(message);

            log.info("📤 Redis 발행 메시지: {}", messageJson);

            redisTemplate.convertAndSend(CHANNEL, messageJson);
        } catch (JsonProcessingException e) {
            log.error("Redis 직렬화 실패: {}", message, e);
            throw new RuntimeException("Redis 직렬화 실패", e);
        }
    }
}
