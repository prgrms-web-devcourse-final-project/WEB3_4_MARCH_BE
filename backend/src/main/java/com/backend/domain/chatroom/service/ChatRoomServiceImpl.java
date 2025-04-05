package com.backend.domain.chatroom.service;

import com.backend.domain.chat.dto.ChatMessage;
import com.backend.domain.chat.dto.MessageType;
import com.backend.domain.chat.service.redis.RedisPublisher;
import com.backend.domain.chat.service.redis.RedisUnreadService;
import com.backend.domain.chatroom.dto.response.ChatRoomResponse;
import com.backend.domain.chatroom.entity.ChatRoom;
import com.backend.domain.chatroom.repository.ChatRoomRepository;
import com.backend.domain.member.entity.Member;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * 채팅방 관련 비즈니스 로직 구현체입니다.
 */
@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final RedisUnreadService redisUnreadService;
    private final RedisPublisher redisPublisher;

    /**
     * 주어진 사용자 ID가 참여 중인 채팅방 목록을 조회합니다.
     * - sender 또는 receiver에 해당하는 모든 방을 가져옵니다.
     *
     * @param memberId 사용자 ID
     * @return 채팅방 응답 리스트
     */
    @Override
    public Page<ChatRoomResponse> getChatRoomsForMember(Long memberId, Pageable pageable) {
        Page<ChatRoom> chatRooms = chatRoomRepository.findAllWithMembers(memberId, pageable);

        for (ChatRoom chatRoom : chatRooms) {
            Hibernate.initialize(chatRoom.getSender());
            Hibernate.initialize(chatRoom.getReceiver());
        }

        return chatRooms.map(chatRoom -> {
            int unreadCount = redisUnreadService.getUnreadCount(memberId, chatRoom.getId());
            return ChatRoomResponse.from(chatRoom, memberId, unreadCount);
        });
    }

    @Override
    public void exitChatRoom(Member deletingMember, Member otherMember) {

        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findBySenderAndReceiverOrReceiverAndSender(
                deletingMember, otherMember, otherMember , deletingMember
                );

        if (optionalChatRoom.isEmpty()) {
            throw new GlobalException(GlobalErrorCode.CHATROOM_NOT_FOUND);
        }

        ChatRoom chatRoom = optionalChatRoom.get();

        // 삭제하는 사용자는 채팅방 참여 목록에서 제거
        chatRoom.removeParticipant(deletingMember);
        chatRoomRepository.save(chatRoom);  // 변경사항 저장

        // 남은 사용자가 있다면 시스템 메시지 전송
        if (chatRoom.hasParticipant(otherMember)) {

            // 시스템 메시지 생성: "{deletingMember}님이 채팅방을 나갔습니다."
            ChatMessage systemMessage = ChatMessage.builder()
                    .roomId(chatRoom.getId())
                    .senderId(deletingMember.getId())
                    .receiverId(otherMember.getId())
                    .content(String.format("%s 님이 채팅방을 나갔습니다.", deletingMember.getNickname()))
                    .type(MessageType.SYSTEM)
                    .sendTime(LocalDateTime.now())
                    .build();

            // Redis로 시스템 메시지 전송
            redisPublisher.publish(systemMessage);
        }
    }
}
