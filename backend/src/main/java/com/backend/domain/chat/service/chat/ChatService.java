package com.backend.domain.chat.service.chat;

import com.backend.domain.chat.dto.ChatMessage;
import com.backend.domain.chat.dto.request.ChatMessageRequest;
import com.backend.domain.chat.dto.response.ChatMessageResponse;
import java.util.List;

public interface ChatService {
    // 클라이언트 메시지 발송 처리
    void sendMessage(ChatMessageRequest chatMessagerequest);

    // Kafka에서 수신된 메시지 처리 및 중계
    void relayMessage(ChatMessage chatMessage);

    // TODO : 추후에 memberId로 변경
    List<ChatMessageResponse> getRoomMessage(Long roomId);
}
