package com.backend.domain.chat.dto.request;

import lombok.Getter;

@Getter
public class ChatMessageRequest {
    private Long chatroomId;
    private String content;
}