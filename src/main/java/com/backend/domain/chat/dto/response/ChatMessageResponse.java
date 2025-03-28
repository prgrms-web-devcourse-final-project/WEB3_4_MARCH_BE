package com.backend.domain.chat.dto.response;

import com.backend.domain.chat.entity.Chat;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
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
                .senderId(chat.getSender())
                .chatContent(chat.getChatContent())
                .sendTime(chat.getSendTime())
                .isRead(chat.isRead())
                .build();
    }
}
