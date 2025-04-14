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
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * 채팅방 관련 비즈니스 로직 구현체입니다.
 */
@Slf4j
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
        log.info("조회 요청한 사용자 ID: {}", memberId);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.error("사용자 ID {}에 해당하는 멤버를 찾을 수 없습니다.", memberId);
                    return new GlobalException(GlobalErrorCode.MEMBER_NOT_FOUND);
                });

        // 일반 회원인지 임시 회원인지 확인
        if (member.getRole() == Role.ROLE_TEMP_USER) {
            log.error("멤버 ID {}가 임시 회원(ROLE_TEMP_USER)이라 채팅방 조회 권한이 없습니다.", memberId);
            throw new GlobalException(GlobalErrorCode.CHATROOM_FORBIDDEN);
        }

        Page<ChatRoom> chatRooms = chatRoomRepository.findAllWithMembers(memberId, pageable);
        log.info("조회된 채팅방 수: {}", chatRooms.getTotalElements());

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
            log.error("멤버 {}는 임시 회원(ROLE_TEMP_USER)이므로 채팅방 나가기 권한이 없습니다.", deletingMember.getId());
            throw new GlobalException(GlobalErrorCode.CHATROOM_FORBIDDEN);
        }

        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findBySenderAndReceiverOrReceiverAndSender(
                deletingMember, otherMember, otherMember, deletingMember
        );

        if (optionalChatRoom.isEmpty()) {
            log.error("멤버 {}와 {} 사이의 채팅방을 찾을 수 없습니다.", deletingMember.getId(), otherMember.getId());
            throw new GlobalException(GlobalErrorCode.CHATROOM_NOT_FOUND);
        }

        ChatRoom chatRoom = optionalChatRoom.get();
        chatRoom.removeParticipant(deletingMember);
        chatRoomRepository.save(chatRoom);
        log.info("멤버 {}가 채팅방에서 제거되었습니다. 남은 참여자: {}", deletingMember.getId(), chatRoom);

        if (chatRoom.hasParticipant(otherMember)) {
            ChatMessage systemMessage = ChatMessage.builder()
                    .roomId(chatRoom.getId())
                    .senderId(deletingMember.getId())
                    .receiverId(otherMember.getId())
                    .content(String.format("%s 님이 채팅방을 나갔습니다.", deletingMember.getNickname()))
                    .type(MessageType.SYSTEM)
                    .sendTime(LocalDateTime.now())
                    .build();
            redisPublisher.publish(systemMessage);
            log.info("시스템 메시지가 전송되었습니다: {}", systemMessage);
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
