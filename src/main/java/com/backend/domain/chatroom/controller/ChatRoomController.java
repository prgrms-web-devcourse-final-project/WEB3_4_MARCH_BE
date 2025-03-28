package com.backend.domain.chatroom.controller;

import com.backend.domain.chatroom.dto.ChatRoomRequest;
import com.backend.domain.chatroom.dto.ChatRoomResponse;
import com.backend.domain.chatroom.service.ChatRoomService;
import com.backend.global.response.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    // TODO : 테스트 겸 채팅방 생성 API (Request에서 채팅방을 만드는 구조라면 삭제 할 예정)
    @PostMapping("/create")
    public ResponseEntity<GenericResponse<ChatRoomResponse>> createChatRoom(@RequestBody ChatRoomRequest request) {
        ChatRoomResponse response = chatRoomService.createChatRoom(
                request.getSenderId(), request.getReceiverId());
        return ResponseEntity.ok(GenericResponse.ok(201, response, "채팅방 생성에 성공하였습니다."));
    }
}
