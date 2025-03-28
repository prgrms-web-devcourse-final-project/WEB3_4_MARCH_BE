package com.backend.domain.chat.service.chat;

import com.backend.domain.chat.dto.ChatMessage;
import com.backend.domain.chat.dto.request.ChatMessageRequest;
import com.backend.domain.chat.dto.response.ChatMessageResponse;
import com.backend.domain.chat.dto.response.ChatRoomMessageResponse;
import com.backend.domain.chat.entity.Chat;
import com.backend.domain.chat.exception.ChatErrorCode;
import com.backend.domain.chat.exception.ChatException;
import com.backend.domain.chat.repository.ChatRepository;
import com.backend.domain.chat.service.kafka.KafkaChatProducer;
import com.backend.domain.chat.service.redis.RedisPublisher;
import com.backend.domain.chatroom.entity.ChatRoom;
import com.backend.domain.chatroom.repository.ChatRoomRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final RedisPublisher redisPublisher;
    private final KafkaChatProducer kafkaChatProducer;

    /**
     * - 메세지 전송 후 저장하는 로직입니다. -
     *
     * @param request
     * @return
     */
    @Override
    public ChatMessageResponse sendMessage(ChatMessageRequest request) {

        ChatRoom chatRoom = chatRoomRepository.findById(request.getChatroomId())
                .orElseThrow(() -> new ChatException(ChatErrorCode.NOT_FOUND_BY_ID));

        Chat chat = Chat.builder()
                .chatRoom(chatRoom)
                .sender(request.getSenderId())
                .chatContent(request.getContent())
                .sendTime(LocalDateTime.now())
                .isRead(false)
                .build();

        Chat savedChat = chatRepository.save(chat);
        return ChatMessageResponse.from(savedChat);
    }

    /**
     * 채팅방 번호 기준으로 안읽은 메시지와 함께 메시지를 불러오는 로직입니다.
     * receiverId를 memberId로 변경 후 페이징 예정입니다.
     */
    @Override
    public ChatRoomMessageResponse getRoomMessage(Long chatRoomId, Long receiverId) {
        List<ChatMessageResponse> messages = chatRepository
                .findByChatRoomIdOrderBySendTimeAsc(chatRoomId)
                .stream()
                .map(ChatMessageResponse::from)
                .toList();

        int unreadCount = chatRepository.countUnreadMessages(chatRoomId, receiverId);

        return ChatRoomMessageResponse.builder()
                .messages(messages)
                .unreadCount(unreadCount)
                .build();
    }

    /**
     * Redis 전송용 DTO 생성 후 WebSocket으로 전송, 저장 / 비동기 처리 메소드입니다.
     */
    @Override
    public void relayMessage(ChatMessageRequest request) {
        ChatRoom chatRoom = chatRoomRepository.findById(request.getChatroomId())
                .orElseThrow(() -> new ChatException(ChatErrorCode.NOT_FOUND_BY_ID));

        Long receiverId = chatRoom.getAnotherUserId(request.getSenderId());

        ChatMessage chatMessage = ChatMessage.builder()
                .roomId(request.getChatroomId())
                .senderId(request.getSenderId())
                .receiverId(receiverId)
                .content(request.getContent())
                .sendTime(LocalDateTime.now())
                .build();

        redisPublisher.publish("chat", chatMessage);
        kafkaChatProducer.sendMessage(request);
    }
}
