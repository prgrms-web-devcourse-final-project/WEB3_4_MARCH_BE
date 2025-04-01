package com.backend.domain.chat.dto.response;

import com.backend.domain.chat.entity.Chat;
import com.backend.domain.member.entity.Member;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 채팅 메시지 응답 DTO입니다.
 * - 클라이언트에게 반환되는 채팅 메시지 데이터를 담습니다.
 * - Chat 엔티티로부터 필요한 필드만 추출하여 구성됩니다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {
    private Long id;
    private Long chatroomId;
    private Long senderId;
    private String chatContent;
    private LocalDateTime sendTime;
    private boolean isRead;

    public static ChatMessageResponse from(Chat chat) {
        return ChatMessageResponse.builder()
                .id(chat.getId())
                .chatroomId(chat.getChatRoom().getId())
                .senderId(chat.getId())
                .chatContent(chat.getChatContent())
                .sendTime(chat.getSendTime())
                .build();
    }
}
