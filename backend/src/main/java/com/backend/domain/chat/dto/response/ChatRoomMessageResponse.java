package com.backend.domain.chat.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomMessageResponse {
    private List<ChatMessageResponse> messages;
    private int unreadCount;

}
