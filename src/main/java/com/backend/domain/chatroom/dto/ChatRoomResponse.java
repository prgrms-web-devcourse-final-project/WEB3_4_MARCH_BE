package com.backend.domain.chatroom.dto;

import com.backend.domain.chatroom.entity.ChatRoom;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomResponse {

    private Long id;
    private Long senderId;
    private Long receiverId;
    private Timestamp createdAt;
    private boolean isBlocked;

    public static ChatRoomResponse from(ChatRoom chatRoom) {
        return ChatRoomResponse.builder()
                .id(chatRoom.getId())
                .senderId(chatRoom.getSenderId())
                .receiverId(chatRoom.getReceiverId())
                .createdAt(chatRoom.getCreatedAt())
                .isBlocked(chatRoom.is_blocked())
                .build();
    }
}
