package com.backend.domain.chat.service.chat;

import com.backend.domain.chat.dto.ChatMessage;
import com.backend.domain.chat.dto.request.ChatMessageRequest;
import com.backend.domain.chat.dto.response.ChatMessageResponse;
import com.backend.domain.chat.entity.Chat;
import com.backend.domain.chat.repository.ChatRepository;
import com.backend.domain.chat.service.kafka.KafkaChatProducer;
import com.backend.domain.chat.service.redis.RedisPublisher;
import com.backend.domain.chatroom.entity.ChatRoom;
import com.backend.domain.chatroom.repository.ChatRoomRepository;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void sendMessage(ChatMessageRequest request) {

        ChatRoom chatRoom = chatRoomRepository.findById(request.getChatroomId())
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.NOT_FOUND_BY_ID));

        ChatMessage chatMessage = ChatMessage
                .builder()
                .roomId(chatRoom.getId())
                .senderId(chatRoom.getSenderId())
                .receiverId(chatRoom.getReceiverId())
                .senderName("")
                .content(request.getContent())
                .sendTime(LocalDateTime.now())
                .build();

        redisPublisher.publish(chatMessage);
        kafkaChatProducer.sendMessage(chatMessage);
    }

    /**
     * Redis 전송용 DTO 생성 후 WebSocket으로 전송, 저장 / 비동기 처리 메소드입니다.
     */
    @Override
    @Transactional
    public void relayMessage(ChatMessage chatMessage) {

        ChatRoom room = chatRoomRepository.findById(chatMessage.getRoomId())
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.NOT_FOUND_BY_ID));

        Chat chat = chatMessage.toEntity(room);

        chatRepository.save(chat);

        redisPublisher.publish(chatMessage);
    }

    /**
     * 채팅방 번호 기준으로 안읽은 메시지와 함께 메시지를 불러오는 로직입니다.
     * receiverId를 memberId로 변경 후 페이징 예정입니다.
     */
    @Override
    @Transactional
    public List<ChatMessageResponse> getRoomMessage(Long roomId) {

        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.NOT_FOUND_BY_ID));

        List<Chat> chats = chatRepository.findByChatRoomIdOrderBySendTimeAsc(roomId);

        return chats.stream()
                .map(ChatMessageResponse::from)
                .collect(Collectors.toList());

    }
}
