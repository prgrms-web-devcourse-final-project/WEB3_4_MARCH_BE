package com.backend.domain.chat.controller;

import com.backend.domain.chat.dto.request.ChatMessageRequest;
import com.backend.domain.chat.dto.response.ChatMessageResponse;
import com.backend.domain.chat.service.chat.ChatService;
import com.backend.domain.chatroom.service.ChatRoomService;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.global.auth.kakao.util.SecurityUtil;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import com.backend.global.response.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 채팅 메시지 관련 컨트롤러입니다.
 * - 채팅방 내 메시지 조회 (REST API)
 * - 채팅 메시지 전송 (WebSocket 메시지 수신)
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("api/chat/chatrooms")
public class ChatController {

    private final ChatRoomService chatRoomService;
    private final ChatService chatService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MemberRepository memberRepository;

    /**
     * 특정 채팅방의 메시지를 조회합니다.
     * - 추후 안 읽은 메시지 수 포함 및 페이징 기능 추가 예정
     *
     * @param roomId 조회할 채팅방 ID
     * @return 해당 채팅방의 메시지 목록
     */
    @GetMapping("/{roomId}")
    public ResponseEntity<GenericResponse<Slice<ChatMessageResponse>>> getMessage(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {

        // 로그인 사용자 ID 추출
        Long currentMemberId = SecurityUtil.getCurrentMemberId();

        // 채팅방 입장 시, 해당 사용자의 안 읽은 메시지를 읽음 처리
        chatService.markMessageAsRead(roomId, currentMemberId);

        // 최신순으로 조회
        Pageable pageable = PageRequest.of(page, size, Sort.by("sendTime").descending());

        Slice<ChatMessageResponse> messages = chatService.getRoomMessage(roomId, currentMemberId ,pageable);
        return ResponseEntity.ok(GenericResponse.ok(messages, "채팅방 메시지 조회"));
    }

    /**
     * WebSocket을 통해 수신된 채팅 메시지를 처리합니다.
     * - 로그인된 사용자 정보를 JWT 기반으로 추출하여 메시지 전송에 사용합니다.
     *
     * @param chatMessageRequest 클라이언트로부터 수신한 메시지 요청 DTO
     */
    @MessageMapping("/{roomId}/message")
    public void sendMessage(@Payload ChatMessageRequest chatMessageRequest) {

        // 로그인 사용자 ID 추출
        Long currentMemberId = SecurityUtil.getCurrentMemberId();

        Member sender = memberRepository.findById(currentMemberId)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.MEMBER_NOT_FOUND));

        chatService.sendMessage(chatMessageRequest, sender);
    }
}
