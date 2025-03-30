package com.backend.domain.chatrequest.dto.response;

import com.backend.domain.chatrequest.entity.ChatRequest;
import com.backend.domain.chatrequest.entity.ChatRequestStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatRequestDto {

    private Long id;

    private Long sendId;

    private Long receiverId;

    private ChatRequestStatus status;

    private LocalDateTime requestedAt;

    public static ChatRequestDto of(ChatRequest entity) {
        return ChatRequestDto.builder()
                .id(entity.getId())
                .sendId(entity.getSender().getId())
                .receiverId(entity.getReceiver().getId())
                .status(entity.getStatus())
                .requestedAt(entity.getRequestedAt())
                .build();
    }


}
