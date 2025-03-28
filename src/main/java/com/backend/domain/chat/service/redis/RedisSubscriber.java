package com.backend.domain.chat.service.redis;

import com.backend.domain.chat.dto.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisSubscriber implements MessageListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String msgBody = new String(message.getBody(), StandardCharsets.UTF_8);
            log.info("🔔 Redis 수신 메시지: {}", msgBody); // ✅ 이거 추가해서 로그 확인
            ChatMessage parsed = objectMapper.readValue(msgBody, ChatMessage.class);

            messagingTemplate.convertAndSend("/sub/chat/" + parsed.getRoomId(), parsed);
            log.info("🚀 WebSocket 전송 완료: /sub/chat/{}", parsed.getRoomId());
        } catch (Exception e) {
            throw new RuntimeException("Redis 메시지 역직렬화 실패", e);
        }

    }
}
