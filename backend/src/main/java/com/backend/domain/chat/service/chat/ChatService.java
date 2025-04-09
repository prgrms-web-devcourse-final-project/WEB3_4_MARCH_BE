package com.backend.domain.chat.service.chat;

import com.backend.domain.chat.dto.ChatMessage;
import com.backend.domain.chat.dto.request.ChatMessageRequest;
import com.backend.domain.chat.dto.response.ChatMessageResponse;
import com.backend.domain.member.entity.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ChatService {
    // 클라이언트 메시지 발송 처리
    void sendMessage(ChatMessageRequest chatMessagerequest, Member currentMember);

    // Kafka에서 수신된 메시지 처리 및 중계
    void relayMessage(ChatMessage chatMessage);

    // 채팅방 메시지 조회
    Slice<ChatMessageResponse> getRoomMessage(Long roomId, Long currentMemberId, Pageable pageable);

    // 채팅방 입장시 읽음 처리
    void markMessageAsRead(Long roomId, Long memberId);
}
