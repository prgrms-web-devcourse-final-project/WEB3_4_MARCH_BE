package com.backend.domain.chat.controller;

import com.backend.domain.chat.dto.request.ChatMessageRequest;
import com.backend.domain.chat.dto.response.ChatMessageResponse;
import com.backend.domain.chat.dto.response.ChatRoomMessageResponse;
import com.backend.domain.chat.service.chat.ChatService;
import com.backend.global.response.GenericResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat/chatrooms")
@Slf4j
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate simpMessagingTemplate;

//    @GetMapping
//    public ResponseEntity<GenericResponse<List<ChatRoomResponse>>> getChatRooms(@Auth)

    @GetMapping("/{roomId}")
    public ResponseEntity<GenericResponse<ChatRoomMessageResponse>> getMessage(
            @PathVariable Long roomId,
            @RequestParam Long receiverId) {
        ChatRoomMessageResponse response = chatService.getRoomMessage(roomId, receiverId);
        return ResponseEntity.ok(GenericResponse.ok(response, "채팅방 메시지 조회 + 안 읽은 수 조회 성공"));
    }

    @MessageMapping("/{roomId}/message")
    public void sendMessage(@DestinationVariable Long roomId, ChatMessageRequest request) {
        ChatMessageResponse response = chatService.sendMessage(request);

        String destination = "/queue/chatrooms/" + roomId;

        log.info("✅ 메시지 전송 완료 - 방 ID: {}, 보낸 사람: {}, 내용: {}",
                roomId, request.getSenderId(), request.getContent());

        simpMessagingTemplate.convertAndSend(destination, response);
    }
}
