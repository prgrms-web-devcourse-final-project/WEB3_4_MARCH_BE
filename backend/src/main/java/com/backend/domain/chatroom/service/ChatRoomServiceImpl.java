package com.backend.domain.chatroom.service;

import com.backend.domain.chat.dto.ChatMessage;
import com.backend.domain.chat.dto.MessageType;
import com.backend.domain.chat.entity.Chat;
import com.backend.domain.chat.repository.ChatRepository;
import com.backend.domain.chat.service.redis.RedisPublisher;
import com.backend.domain.chat.service.redis.RedisUnreadService;
import com.backend.domain.chatroom.dto.response.ChatRoomResponse;
import com.backend.domain.chatroom.entity.ChatRoom;
import com.backend.domain.chatroom.repository.ChatRoomRepository;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.entity.Role;
import com.backend.domain.member.repository.MemberRepository;
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

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final RedisUnreadService redisUnreadService;
    private final RedisPublisher redisPublisher;
    private final MemberRepository memberRepository;

    /**
     * 주어진 사용자 ID가 참여 중인 채팅방 목록을 조회합니다.
     * - sender 또는 receiver에 해당하는 모든 방을 가져옵니다.
     *
     * @param memberId 사용자 ID
     * @return 채팅방 응답 리스트
     */
    @Override
    public Page<ChatRoomResponse> getChatRoomsForMember(Long memberId, Pageable pageable) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.MEMBER_NOT_FOUND));

        // 일반 회원인지 임시 회원인지 확인
        if (member.getRole() == Role.ROLE_TEMP_USER) {
            throw new GlobalException(GlobalErrorCode.CHATROOM_FORBIDDEN);
        }

        Page<ChatRoom> chatRooms = chatRoomRepository.findAllWithMembers(memberId, pageable);

        // LAZY 초기화
        for (ChatRoom chatRoom : chatRooms) {
            Hibernate.initialize(chatRoom.getSender());
            Hibernate.initialize(chatRoom.getReceiver());
        }

        return chatRooms.map(chatRoom -> {
            int unreadCount = redisUnreadService.getUnreadCount(memberId, chatRoom.getId());
            return convertToChatRoomSummaryDTO(chatRoom, memberId, unreadCount);
        });
    }

    @Override
    public void exitChatRoom(Member deletingMember, Member otherMember) {

        if (deletingMember.getRole() == Role.ROLE_TEMP_USER) {
            throw new GlobalException(GlobalErrorCode.CHATROOM_FORBIDDEN);
        }

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

    // ChatRoom 매핑
    public ChatRoomResponse convertToChatRoomSummaryDTO(ChatRoom chatRoom, Long currentMemberId, int unreadCount) {
        // 1. 현재 로그인한 사용자의 ID(currentMemberId)에 따라 상대방을 결정합니다.
        Member opponent = chatRoom.getSender().getId().equals(currentMemberId)
                ? chatRoom.getReceiver()
                : chatRoom.getSender();

        // 2. 상대방 정보(UserSummary) 생성
        ChatRoomResponse.MemberSummary memberSummary = ChatRoomResponse.MemberSummary.builder()
                .id(opponent.getId())
                .name(opponent.getNickname())
                // 프로필 이미지가 존재하면 그 URL, 없으면 null 또는 기본 URL
                .image(opponent.getProfileImage() != null ? opponent.getProfileImage().getUrl() : null)
                .build();

        // 3. 마지막 메시지 정보(MessageSummary) 생성
        // 최신 메시지 조회 (ChatRepository를 이용)
        Chat lastChat = chatRepository.findFirstByChatRoomIdOrderBySendTimeDesc(chatRoom.getId())
                .orElse(null);

        ChatRoomResponse.MessageSummary messageSummary = ChatRoomResponse.MessageSummary.builder()
                .text(lastChat != null ? lastChat.getChatContent() : "")
                .timestamp(lastChat != null ? lastChat.getSendTime() : LocalDateTime.now())
                .isRead(lastChat != null && lastChat.isRead())
                .isFromMe(lastChat != null && lastChat.getSender().getId().equals(currentMemberId))
                .build();

        // 4. 최종 ChatRoomSummaryDTO 생성
        return ChatRoomResponse.builder()
                .id(chatRoom.getId())
                .opponent(memberSummary)
                .message(messageSummary)
                .unreadCount(unreadCount)
                .build();
    }
}
