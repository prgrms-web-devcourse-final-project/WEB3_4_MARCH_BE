package com.backend.domain.chatroom.service;

import com.backend.domain.chatroom.dto.response.ChatRoomResponse;
import java.util.List;

public interface ChatRoomService {
    ChatRoomResponse createChatRoom(Long senderId, Long receiverId);
    List<ChatRoomResponse> getChatRoomsForMember(Long memberId);
}
