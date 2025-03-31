package com.backend.domain.chat.service.kafka;

import com.backend.domain.chat.dto.ChatMessage;
import com.backend.domain.chat.entity.Chat;
import com.backend.domain.chat.repository.ChatRepository;
import com.backend.domain.chat.service.redis.RedisPublisher;
import com.backend.domain.chatroom.entity.ChatRoom;
import com.backend.domain.chatroom.repository.ChatRoomRepository;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaChatConsumer {

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final RedisPublisher redisPublisher;

    @KafkaListener(
            topics = "chat-massage",
            groupId = "chat-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(ChatMessage chatMessage) {
        try {
            ChatRoom room = chatRoomRepository.findById(chatMessage.getRoomId())
                    .orElseThrow(() -> new GlobalException(GlobalErrorCode.NOT_FOUND_BY_ID));

            Chat chat = chatMessage.toEntity(room);

            chatRepository.save(chat);

            redisPublisher.publish(chatMessage);

        } catch (Exception e) {
            log.error("KafkaConsumer 오류", e);
        }
    }
}
