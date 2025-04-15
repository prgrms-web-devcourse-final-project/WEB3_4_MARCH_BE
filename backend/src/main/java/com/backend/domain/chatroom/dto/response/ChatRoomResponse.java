package com.backend.domain.chatroom.dto.response;

import com.backend.domain.chat.entity.Chat;
import com.backend.domain.chatroom.entity.ChatRoom;
import com.backend.domain.member.entity.Member;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.cglib.core.Local;

/**
 * 채팅방 응답 DTO입니다.
 * - 채팅방 기본 정보 및 상대방 사용자 정보를 제공합니다.
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomResponse {

    private Long id;
    private MemberSummary opponent;

    @JsonProperty("lastMessage")
    private MessageSummary message;
    private int unreadCount;

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberSummary {

        private Long id;
        private String name;
        private String image;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageSummary {

        private String text;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "a hh:mm", timezone = "Asia/Seoul")
        private LocalDateTime timestamp;

        private boolean isRead;

        private boolean isFromMe;
    }
}