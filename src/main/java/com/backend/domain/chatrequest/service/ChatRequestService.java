package com.backend.domain.chatrequest.service;

import com.backend.domain.chatrequest.dto.ChatRequestRequest;
import com.backend.domain.chatrequest.dto.ChatRequestResponse;
import com.backend.domain.chatroom.dto.ChatRoomResponse;

public interface ChatRequestService {
    ChatRequestResponse createRequest(ChatRequestRequest request);
    ChatRoomResponse acceptRequest(Long requestId);
}

