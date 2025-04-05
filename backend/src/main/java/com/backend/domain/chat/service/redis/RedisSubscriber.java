package com.backend.domain.chat.service.redis;

import com.backend.domain.chat.dto.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Redis로부터 수신된 채팅 메시지를 WebSocket을 통해 구독자에게 전달하는 클래스입니다.
 * - 채팅방 구독 경로로 메시지를 전송합니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RedisSubscriber {

    // WebSocket 메시지 전송 템플릿
    private final @Lazy SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Redis로부터 전달받은 메시지를 WebSocket 구독 채널로 전송합니다.
     *
     * @param message 수신한 채팅 메시지 DTO
     */
    public void handleMessage(ChatMessage message) {
        log.info("📨 Redis 메시지 수신됨: {}", message);

        try {
            String destination = "/sub/chat/" + message.getRoomId();

            // 🔽 JSON 직렬화된 메시지 로그 출력
            log.info("📬 WebSocket 전송 전 메시지(JSON): {}", objectMapper.writeValueAsString(message));

            log.info("📤 WebSocket 전송 경로: {}, 내용: {}", destination, message.getContent());
            messagingTemplate.convertAndSend(destination, message);
        } catch (Exception e) {
            log.error("🚨 WebSocket 전송 실패", e);
        }
    }

    @PostConstruct
    public void init() {
        log.info("✅ RedisSubscriber 초기화됨");
    }
}
