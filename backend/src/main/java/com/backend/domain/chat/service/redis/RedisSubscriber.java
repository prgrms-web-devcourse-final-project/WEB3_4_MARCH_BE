package com.backend.domain.chat.service.redis;

import com.backend.domain.chat.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisSubscriber {

    private final @Lazy SimpMessagingTemplate messagingTemplate;

    public void handleMessage(ChatMessage message) {
        messagingTemplate.convertAndSend("/api/sub/chat" + message.getRoomId(), message);
    }
}
