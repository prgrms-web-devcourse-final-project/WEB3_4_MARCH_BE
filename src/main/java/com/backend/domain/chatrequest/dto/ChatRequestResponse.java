package com.backend.domain.chatrequest.dto;

import com.backend.domain.chatrequest.entity.ChatRequestStatus;
import java.time.LocalDateTime;
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
    private LocalDateTime requestAt;
}
