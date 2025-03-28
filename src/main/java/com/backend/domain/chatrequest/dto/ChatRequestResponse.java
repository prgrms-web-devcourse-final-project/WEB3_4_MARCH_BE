package com.backend.domain.chatrequest.dto;

import com.backend.domain.chatrequest.entity.ChatRequest;
import com.backend.domain.chatrequest.entity.ChatRequestStatus;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRequestResponse {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private ChatRequestStatus status;
    private Timestamp requestAt;

    public static ChatRequestResponse from(ChatRequest request) {
        return ChatRequestResponse.builder()
                .id(request.getId())
                .senderId(request.getSender())
                .receiverId(request.getReceiver())
                .status(request.getStatus())
                .requestAt(request.getRequestedAt())
                .build();
    }
}
