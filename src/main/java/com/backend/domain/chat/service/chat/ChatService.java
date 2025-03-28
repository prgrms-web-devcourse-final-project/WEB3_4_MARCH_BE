package com.backend.domain.chat.service.chat;

import com.backend.domain.chat.dto.request.ChatMessageRequest;
import com.backend.domain.chat.dto.response.ChatMessageResponse;
import com.backend.domain.chat.dto.response.ChatRoomMessageResponse;

public interface ChatService {
    ChatMessageResponse sendMessage(ChatMessageRequest request);

    // TODO : 추후에 memberId로 변경
    ChatRoomMessageResponse getRoomMessage(Long chatRoomId, Long receiverId);

    void relayMessage(ChatMessageRequest request);
}
