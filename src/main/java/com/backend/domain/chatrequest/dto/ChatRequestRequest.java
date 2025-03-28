package com.backend.domain.chatrequest.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRequestRequest {
    private Long senderId;
    private Long receiverId;
}
