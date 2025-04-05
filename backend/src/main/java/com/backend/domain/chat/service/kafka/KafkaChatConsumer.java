package com.backend.domain.chat.service.kafka;

import com.backend.domain.chat.dto.ChatMessage;
import com.backend.domain.chat.entity.Chat;
import com.backend.domain.chat.repository.ChatRepository;
import com.backend.domain.chat.service.redis.RedisPublisher;
import com.backend.domain.chatroom.entity.ChatRoom;
import com.backend.domain.chatroom.repository.ChatRoomRepository;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka를 통해 수신된 채팅 메시지를 처리하는 Consumer 클래스입니다.
 * - Kafka로부터 ChatMessage를 수신하고
 * - 해당 메시지를 DB에 저장한 후
 * - Redis를 통해 실시간 클라이언트 동기화를 수행합니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaChatConsumer {

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final RedisPublisher redisPublisher;
    private final MemberRepository memberRepository;

    /**
     * Kafka에서 "chat-massage" 토픽의 메시지를 수신합니다.
     *
     * @param chatMessage Kafka를 통해 전달받은 채팅 메시지 DTO
     */
    @KafkaListener(
            topics = "chat-massage",
            groupId = "chat-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(ChatMessage chatMessage) {
        try {
            // 채팅방 조회
            ChatRoom room = chatRoomRepository.findById(chatMessage.getRoomId())
                    .orElseThrow(() -> new GlobalException(GlobalErrorCode.CHATROOM_NOT_FOUND));

            // 발신자 조회
            Member sender = memberRepository.findById(chatMessage.getSenderId())
                    .orElseThrow(() -> new GlobalException(GlobalErrorCode.MEMBER_NOT_FOUND));

            // Chat 엔티티로 변환 및 저장
            Chat chat = chatMessage.toEntity(room, sender);

            chatRepository.save(chat);

            redisPublisher.publish(chatMessage);

        } catch (Exception e) {
            log.error("KafkaConsumer 오류", e);
        }
    }
}
