package com.backend.domain.chat.service.redis;

import com.backend.domain.chat.dto.ChatMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisPublisher {
    private static final String CHANNEL = "chat-message";  // Redis Pub/Sub 채널명

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public void publish(ChatMessage message) {
        try {
            String messageJson = objectMapper.writeValueAsString(message);
            redisTemplate.convertAndSend(CHANNEL, messageJson);
        } catch (JsonProcessingException e) {
            log.error("Redis 직렬화 실패: {}", message, e);
            throw new RuntimeException("Redis 직렬화 실패", e);
        }
    }
}
