package com.backend.domain.chatroom.dto.response;

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

    // 추후 구현 예정 필드
    private String opponentNickname;
    private String opponentProfileImgUrl;

    public static ChatRoomResponse from(ChatRoom chatRoom /* String nickname, String profileUrl */) {
        return ChatRoomResponse.builder()
                .id(chatRoom.getId())
                .senderId(chatRoom.getSenderId())
                .receiverId(chatRoom.getReceiverId())
                .createdAt(chatRoom.getCreatedAt())
                // .opponentNickname(nickname)
                // .opponentProfileImgUrl(profileUrl)
                .opponentNickname("")
                .opponentProfileImgUrl("")
                .build();
    }
}
