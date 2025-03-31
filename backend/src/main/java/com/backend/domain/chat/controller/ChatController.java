package com.backend.domain.chat.controller;

import com.backend.domain.chat.dto.request.ChatMessageRequest;
import com.backend.domain.chat.dto.response.ChatMessageResponse;
import com.backend.domain.chat.service.chat.ChatService;
import com.backend.domain.chatroom.dto.response.ChatRoomResponse;
import com.backend.domain.chatroom.service.ChatRoomService;
import com.backend.global.response.GenericResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/chat/chatrooms")
public class ChatController {

    private final ChatRoomService chatRoomService;
    private final ChatService chatService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping
    public ResponseEntity<GenericResponse<List<ChatRoomResponse>>> getChatRooms() {
        Long currentMemberId = 1L; // TODO : 인증 처리 필요
        List<ChatRoomResponse> chatRooms = chatRoomService.getChatRoomsForMember(currentMemberId);

        return ResponseEntity.ok(GenericResponse.ok(chatRooms, "조회를 성공했습니다."));
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<GenericResponse<List<ChatMessageResponse>>> getMessage(
            @PathVariable Long roomId) {
        List<ChatMessageResponse> messages = chatService.getRoomMessage(roomId);
        return ResponseEntity.ok(GenericResponse.ok(messages, "채팅방 메시지 조회 + 안 읽은 수 조회 성공"));
    }

    @MessageMapping("/{roomId}/message")
    public void sendMessage(ChatMessageRequest chatMessageRequest) {
        chatService.sendMessage(chatMessageRequest);
    }
}
