package com.backend.domain.chatroom.service;

import com.backend.domain.chatrequest.entity.ChatRequest;
import com.backend.domain.chatrequest.repository.ChatRequestRepository;
import com.backend.domain.chatroom.dto.response.ChatRoomResponse;
import com.backend.domain.chatroom.entity.ChatRoom;
import com.backend.domain.chatroom.repository.ChatRoomRepository;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRequestRepository chatRequestRepository;

    @Override
    @Transactional
    public ChatRoomResponse createChatRoom(Long senderId, Long receiverId) {

        chatRoomRepository.findRoomByMembers(senderId, receiverId).ifPresent(
                room -> {throw new GlobalException(
                        GlobalErrorCode.DUPLICATE_CHAT_REQUEST);
                }
        );

        ChatRequest chatRequest = chatRequestRepository.findRequestByMembers(senderId, receiverId)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.NOT_FOUND_BY_REQUEST));

        ChatRoom chatRoom = ChatRoom.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .chatRequest(chatRequest)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .is_blocked(false)
                .build();


        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);

        return ChatRoomResponse.from(chatRoom);
    }

    @Override
    public List<ChatRoomResponse> getChatRoomsForMember(Long memberId) {
        List<ChatRoom> chatRooms = chatRoomRepository.findBySenderIdOrReceiverId(memberId, memberId);

        return chatRooms.stream()
                .map(ChatRoomResponse::from)
                .collect(Collectors.toList());
    }
}
