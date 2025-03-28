package com.backend.domain.chatrequest.controller;

import com.backend.domain.chatrequest.service.ChatRequestService;
import com.backend.domain.chatroom.dto.ChatRoomResponse;
import com.backend.global.response.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/chat-request")
public class ChatRequestController {

    private final ChatRequestService chatRequestService;

    // TODO : Request 머지하면 삭제 예정
    @PostMapping("/accept/{id}")
    public GenericResponse<ChatRoomResponse> acceptChatRequest(@PathVariable Long id) {
        ChatRoomResponse response = chatRequestService.acceptRequest(id);
        return GenericResponse.ok(response, "채팅 요청을 수락하고 채팅방이 생성되었습니다.");
    }
}
