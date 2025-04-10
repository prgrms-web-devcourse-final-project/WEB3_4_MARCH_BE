package com.backend.domain.chat.service.chat;

import com.backend.domain.chat.dto.ChatMessage;
import com.backend.domain.chat.dto.MessageType;
import com.backend.domain.chat.dto.request.ChatMessageRequest;
import com.backend.domain.chat.dto.response.ChatMessageResponse;
import com.backend.domain.chat.entity.Chat;
import com.backend.domain.chat.repository.ChatRepository;
import com.backend.domain.chat.service.kafka.KafkaChatProducer;
import com.backend.domain.chat.service.redis.RedisPublisher;
import com.backend.domain.chat.service.redis.RedisUnreadService;
import com.backend.domain.chatroom.entity.ChatRoom;
import com.backend.domain.chatroom.repository.ChatRoomRepository;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 채팅 관련 기능을 처리하는 서비스 구현 클래스입니다.
 * - 메세지 전송
 * - 메세지 저장
 * - 채팅방 메시지 조회
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final RedisPublisher redisPublisher;
    private final KafkaChatProducer kafkaChatProducer;
    private final MemberRepository memberRepository;
    private final RedisUnreadService redisUnreadService;

    /**
     * 사용자가 보낸 메시지를 처리합니다.
     * - 채팅방을 조회하고, 수신자 정보를 확인한 뒤
     * - ChatMessage 객체를 생성하여 Redis와 Kafka로 전송합니다.
     * - 전송에 실패하면, 최대 3회 재전송합니다.
     *
     * @param request 클라이언트로부터 받은 채팅 메시지 요청
     * @param sender 현재 로그인한 사용자 (메시지 전송자)
     */
    @Override
    @Transactional
    public void sendMessage(ChatMessageRequest request, Member sender) {

        log.info("chatroomId from request: {}", request.getChatroomId());
        ChatRoom chatRoom = chatRoomRepository.findById(request.getChatroomId())
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.CHATROOM_NOT_FOUND));

        if (!chatRoom.hasParticipant(sender)) {
            throw new GlobalException(GlobalErrorCode.CHATROOM_FORBIDDEN);
        }

        // 수신자 찾기
        Member receiver = chatRoom.getAnotherMember(sender);

        // Redis에서 안 읽은 메시지 수 증가
        redisUnreadService.increaseUnreadCount(receiver.getId(), chatRoom.getId());

        // 후속 처리 : 메시지 생성, DB 저장, 메시지 발행을 별도의 재시도 대상 메서드에서 처리
        processMessageDelivery(request, sender, chatRoom, receiver);
    }

    @Retryable(
            retryFor = { Exception.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    private void processMessageDelivery(ChatMessageRequest request, Member sender,
            ChatRoom chatRoom, Member receiver) {

        // 채팅 메시지 생성
        ChatMessage chatMessage = ChatMessage.builder()
                .uuid(UUID.randomUUID().toString())
                .roomId(chatRoom.getId())
                .senderId(sender.getId())
                .receiverId(receiver.getId())
                .type(MessageType.CHAT)
                .content(request.getContent())
                .sendTime(LocalDateTime.now())
                .isRead(false)
                .build();

        // DB에 채팅 메시지 저장 (Chat 엔티티로 변환 후 저장)
        Chat chat = chatMessage.toEntity(chatRoom, sender);

        // 2-1 UUID 중복 검증: 같은 UUID를 가진 메시지가 이미 존재하는지 확인
        if (chatRepository.existsByUuid(chat.getUuid())) {
            log.info("이미 처리된 메시지입니다. UUID : {}", chat.getUuid());
            // 중복 저장 방지하기 위해 저장을 건너뛰고, 로그를 남김
            return;
        }

        // 그 후 저장
        chatRepository.save(chat);

        // 메시지 발행 (Redis), 메시지 저장 (Kafka)
        redisPublisher.publish(chatMessage);
        kafkaChatProducer.sendMessage(chatMessage);
    }

    /**
     * 먼저 sendTime의 null 값을 .now로 채워준 후,
     * Kafka를 통해 수신된 메시지를 실제 DB에 저장하고,
     * 실시간 동기화를 위해 Redis에 다시 발행합니다.
     *
     * @param chatMessage Kafka에서 수신한 채팅 메시지 DTO
     */
    @Override
    @Transactional
    public void relayMessage(ChatMessage chatMessage) {

        if (chatMessage.getSendTime() == null) {
            chatMessage = ChatMessage.builder()
                    .uuid(chatMessage.getUuid())
                    .roomId(chatMessage.getRoomId())
                    .senderId(chatMessage.getSenderId())
                    .receiverId(chatMessage.getReceiverId())
                    .type(MessageType.CHAT)
                    .content(chatMessage.getContent())
                    .sendTime(LocalDateTime.now())
                    .isRead(false)
                    .build();
        }

        ChatRoom room = chatRoomRepository.findById(chatMessage.getRoomId())
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.CHATROOM_NOT_FOUND));

        Member sender = memberRepository.findById(chatMessage.getSenderId())
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.MEMBER_NOT_FOUND));

        Chat chat = chatMessage.toEntity(room, sender);

        chatRepository.save(chat);
        redisPublisher.publish(chatMessage);
    }

    /**
     * 특정 채팅방의 모든 메시지를 조회합니다.
     * - 메시지는 발송 시간 기준으로 정렬되며,
     * - 추후 페이징 및 안 읽은 메시지 수와 함께 확장 가능합니다.
     *
     * @param roomId 조회할 채팅방 ID
     * @return 해당 채팅방의 메시지 목록
     */
    @Override
    @Transactional(readOnly = true)
    public Slice<ChatMessageResponse> getRoomMessage(Long roomId, Long currentMemberId, Pageable pageable) {

        // 채팅방 있는지 확인
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.CHATROOM_NOT_FOUND));

        // 로그인한 사용자가 채팅방의 참여자가 아니면 에러 발생
        if (!chatRoom.getSender().getId().equals(currentMemberId)
            && !chatRoom.getReceiver().getId().equals(currentMemberId)) {
            throw new GlobalException(GlobalErrorCode.CHATROOM_FORBIDDEN);
        }

        // 안 읽은 메시지 수 초기화
        redisUnreadService.resetUnreadCount(currentMemberId, chatRoom.getId());

        Slice<Chat> chatSlice = chatRepository.findByChatRoomId(roomId, pageable);

        return chatSlice.map(ChatMessageResponse::from);
    }

    /**
     * 주어진 채팅방(roomId)에서 특정 사용자(memberId)가 수신한 아직 읽지 않은 메시지를 읽음 처리합니다.
     * - 채팅방이 존재하는지 확인하고, 존재하지 않으면 예외 발생
     * - 지정된 사용자가 해당 채팅방의 참여자인지 검증합니다. 참여자가 아니라면 예외 발생
     * - 채팅방 내 해당 사용자에게 전달된 아직 읽지 않은 메시지들의 isRead 상태를 업데이트하며,
     * - 업데이트된 메시지 건수를 로그에 기록합니다.
     *
     * @param roomId   읽음 처리를 할 채팅방의 ID
     * @param memberId 읽음 처리를 적용할 사용자의 ID
     */
    @Transactional
    public void markMessageAsRead(Long roomId, Long memberId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.CHATROOM_NOT_FOUND));

        // 사용자가 채팅방의 참여자인지 확인
        if (!chatRoom.getSender().getId().equals(memberId) &&
        !chatRoom.getReceiver().getId().equals(memberId)) {
            throw new GlobalException(GlobalErrorCode.CHATROOM_FORBIDDEN);
        }

        // 채팅방 내 해당 사용자에게 전달된 아직 읽지 않은 메시지 업데이트
        int updateCount = chatRepository.updateMessagesToRead(roomId, memberId);
        log.info("{}개의 메시지가 읽음 처리 되었습니다.", updateCount);
    }
}
