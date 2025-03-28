package com.backend.domain.chatroom.service;

import com.backend.domain.chat.exception.ChatErrorCode;
import com.backend.domain.chat.exception.ChatException;
import com.backend.domain.chatrequest.entity.ChatRequest;
import com.backend.domain.chatrequest.repository.ChatRequestRepository;
import com.backend.domain.chatroom.dto.ChatRoomResponse;
import com.backend.domain.chatroom.entity.ChatRoom;
import com.backend.domain.chatroom.repository.ChatRoomRepository;
import java.sql.Timestamp;
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

        // TODO : Request 머지 후 ErrorCode 생성
        chatRoomRepository.findRoomByMembers(senderId, receiverId).ifPresent(
                room -> {throw new ChatException(
                        ChatErrorCode.DUPLICATE_CHAT_REQUEST);
                }
        );

        // TODO : Request 머지 후 ErrorCode 생성
        ChatRequest chatRequest = chatRequestRepository.findRequestByMembers(senderId, receiverId)
                .orElseThrow(() -> new ChatException(ChatErrorCode.NOT_FOUND_BY_REQUEST));

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
}
