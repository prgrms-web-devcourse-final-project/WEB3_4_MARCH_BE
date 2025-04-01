package com.backend.domain.chatroom.service;

import com.backend.domain.chatrequest.entity.ChatRequest;
import com.backend.domain.chatrequest.repository.ChatRequestRepository;
import com.backend.domain.chatroom.dto.response.ChatRoomResponse;
import com.backend.domain.chatroom.entity.ChatRoom;
import com.backend.domain.chatroom.repository.ChatRoomRepository;
import com.backend.domain.member.entity.Member;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 채팅방 관련 비즈니스 로직 구현체입니다.
 */
@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRequestRepository chatRequestRepository;

    /**
     * 채팅 요청을 수락한 후 채팅방을 생성합니다.
     * - 중복 채팅방이 존재할 경우 예외를 발생시킵니다.
     * - 기존 ChatRequest가 존재해야만 채팅방을 만들 수 있습니다.
     *
     * @param sender 채팅 요청을 보낸 사용자
     * @param receiver 채팅 요청을 받은 사용자
     * @return 생성된 채팅방 응답 DTO
     */
    @Override
    @Transactional
    public ChatRoomResponse createChatRoom(Member sender, Member receiver) {

        chatRoomRepository.findBySenderAndReceiverOrReceiverAndSender(sender, receiver, receiver, sender)
                .ifPresent(room -> {
                    throw new GlobalException(GlobalErrorCode.DUPLICATE_CHAT_REQUEST);
                });

        ChatRequest chatRequest = chatRequestRepository.findRequestByMembers(sender, receiver)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.NOT_FOUND_CHAT_REQUEST));

        ChatRoom chatRoom = ChatRoom.builder()
                .sender(sender)
                .receiver(receiver)
                .chatRequest(chatRequest)
                .createAt(LocalDateTime.now())
                .build();


        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);

        return ChatRoomResponse.from(chatRoom);
    }

    /**
     * 주어진 사용자 ID가 참여 중인 채팅방 목록을 조회합니다.
     * - sender 또는 receiver에 해당하는 모든 방을 가져옵니다.
     *
     * @param memberId 사용자 ID
     * @return 채팅방 응답 리스트
     */
    @Override
    public List<ChatRoomResponse> getChatRoomsForMember(Long memberId) {
        List<ChatRoom> chatRooms = chatRoomRepository.findBySenderIdOrReceiverId(memberId, memberId);

        return chatRooms.stream()
                .map(ChatRoomResponse::from)
                .collect(Collectors.toList());
    }
}
