package com.backend.chat.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ChatMessage {
    private final String roomId;
    private final String sender;
    private final String content;
}
