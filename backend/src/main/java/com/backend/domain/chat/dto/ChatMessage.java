package com.backend.domain.chat.dto;

import com.backend.domain.chat.entity.Chat;
import com.backend.domain.chatroom.entity.ChatRoom;
import com.backend.domain.member.entity.Member;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Redis 및 Kafka 전송에 사용되는 채팅 메시지 전용 DTO입니다.
 * - WebSocket 메시지 전송 및 비동기 메시지 처리에 사용됩니다.
 * - Entity와 분리된 경량 메시지 객체입니다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor

// 양방향 전송 DTO
public class ChatMessage {

    @JsonProperty("chatroomId")
    private Long roomId;

    private Long senderId;
    private Long receiverId;
    private String content;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime sendTime;

    /**
     * ChatMessage DTO를 Chat 엔티티로 변환합니다.
     * - DB 저장 시 사용됩니다.
     *
     * @param room 채팅방 엔티티
     * @param sender 메시지 전송자 (Member 엔티티)
     * @return Chat 엔티티 객체
     */
    public Chat toEntity(ChatRoom room, Member sender) {
        return new Chat(
                room,
                sender,
                this.content,
                this.sendTime != null ? this.sendTime : LocalDateTime.now()
        );
    }


}
