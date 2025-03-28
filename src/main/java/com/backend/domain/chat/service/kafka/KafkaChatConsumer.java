package com.backend.domain.chat.service.kafka;

import com.backend.domain.chat.dto.request.ChatMessageRequest;
import com.backend.domain.chat.entity.Chat;
import com.backend.domain.chat.exception.ChatErrorCode;
import com.backend.domain.chat.exception.ChatException;
import com.backend.domain.chat.repository.ChatRepository;
import com.backend.domain.chatroom.entity.ChatRoom;
import com.backend.domain.chatroom.repository.ChatRoomRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaChatConsumer {

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "chat-messages", groupId = "chat-group")
    public void consume(String message) {
        try {
            ChatMessageRequest request = objectMapper.readValue(message, ChatMessageRequest.class);

            ChatRoom chatRoom = chatRoomRepository.findById(request.getChatroomId())
                    .orElseThrow(() -> new ChatException(ChatErrorCode.NOT_FOUND_BY_ID));

            Chat chat = Chat.builder()
                    .chatRoom(chatRoom)
                    .sender(request.getSenderId())
                    .chatContent(request.getContent())
                    .sendTime(LocalDateTime.now())
                    .isRead(false)
                    .build();

            chatRepository.save(chat);

        } catch (Exception e) {
            log.error("KafkaConsumer 오류", e);
        }
    }
}
