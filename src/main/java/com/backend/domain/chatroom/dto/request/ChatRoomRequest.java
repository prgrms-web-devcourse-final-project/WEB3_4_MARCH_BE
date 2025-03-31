package com.backend.domain.chatroom.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomRequest {
    private Long senderId;
    private Long receiverId;
}
