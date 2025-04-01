package com.backend.domain.chatroom.controller;

import com.backend.domain.chatroom.dto.request.ChatRoomRequest;
import com.backend.domain.chatroom.dto.response.ChatRoomResponse;
import com.backend.domain.chatroom.service.ChatRoomService;
import com.backend.domain.member.entity.Member;
import com.backend.global.response.GenericResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 채팅방 관련 REST API 컨트롤러입니다.
 * - 채팅방 목록 조회
 * - (테스트용) 채팅방 생성
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("api/chat")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    /**
     * 현재 사용자가 참여 중인 모든 채팅방을 조회합니다.
     * - sender 또는 receiver가 현재 로그인한 사용자일 경우 포함됩니다.
     * - 현재는 memberId를 하드코딩하며, 추후 인증 처리 예정입니다.
     *
     * @return 사용자의 채팅방 목록
     */
    @GetMapping
    public ResponseEntity<GenericResponse<List<ChatRoomResponse>>> getChatRooms() {
        Long currentMemberId = 1L; // TODO : 인증 처리 필요
        List<ChatRoomResponse> chatRooms = chatRoomService.getChatRoomsForMember(currentMemberId);

        return ResponseEntity.ok(GenericResponse.ok(chatRooms, "조회를 성공했습니다."));
    }

    /**
     * (테스트용) 채팅방을 직접 생성하는 API입니다.
     * - 실제 운영에서는 채팅 요청(ChatRequest)을 수락할 때 자동 생성되므로,
     *   이 API는 테스트 또는 초기 구조 확인 용도로만 사용됩니다.
     *
     * @param request 채팅방 생성 요청 (sender, receiver)
     * @return 생성된 채팅방 정보
     */
    // TODO : 테스트 겸 채팅방 생성 API (Request에서 채팅방을 만드는 구조라면 삭제 할 예정)
    @PostMapping("/create")
    public ResponseEntity<GenericResponse<ChatRoomResponse>> createChatRoom(@RequestBody ChatRoomRequest request) {
        ChatRoomResponse response = chatRoomService.createChatRoom(
                request.getSenderId(), request.getReceiverId());
        return ResponseEntity.ok(GenericResponse.ok(201, response, "채팅방 생성에 성공하였습니다."));
    }
}
