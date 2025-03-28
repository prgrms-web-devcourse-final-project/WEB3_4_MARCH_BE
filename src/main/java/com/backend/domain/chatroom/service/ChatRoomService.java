package com.backend.domain.chatroom.service;

import com.backend.domain.chatroom.dto.ChatRoomResponse;

public interface ChatRoomService {
    ChatRoomResponse createChatRoom(Long senderId, Long receiverId);
}
