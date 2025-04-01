package com.backend.domain.chatroom.dto.request;

import com.backend.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomRequest {
    private Member senderId;
    private Member receiverId;
}
