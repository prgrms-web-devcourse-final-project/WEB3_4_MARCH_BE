package com.backend.domain.chat.dto;

import com.backend.domain.chat.entity.Chat;
import com.backend.domain.chatroom.entity.ChatRoom;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor

// 양방향 전송 DTO
public class ChatMessage {
    private Long roomId;
    private Long senderId;
    private Long receiverId;
    private String senderName;
    private String content;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime sendTime;

    public Chat toEntity(ChatRoom room) {
        return new Chat(
                room,
                this.senderId,
                this.content,
                LocalDateTime.now()
        );
    }

    public ChatMessage toChatMessage(ChatRoom room) {
        return ChatMessage.builder()
                .roomId(room.getId())
                .senderId(this.senderId)
                .receiverId(room.getAnotherUserId(this.senderId))
                .senderName("")
                .content(this.content)
                .sendTime(LocalDateTime.now())
                .build();
    }
}
