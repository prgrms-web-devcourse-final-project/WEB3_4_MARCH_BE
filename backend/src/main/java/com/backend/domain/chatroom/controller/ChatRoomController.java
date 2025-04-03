package com.backend.domain.chatroom.controller;

import com.backend.domain.chatroom.dto.response.ChatRoomResponse;
import com.backend.domain.chatroom.service.ChatRoomService;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.global.auth.kakao.util.SecurityUtil;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import com.backend.global.response.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 채팅방 관련 REST API 컨트롤러입니다.
 * - 채팅방 목록 조회
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("api/chat/chatrooms")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final MemberRepository memberRepository;

    /**
     * 현재 사용자가 참여 중인 모든 채팅방을 조회합니다.
     * - sender 또는 receiver가 현재 로그인한 사용자일 경우 포함됩니다.
     * - SecurityUtil을 사용하여 현재 인증된 사용자의 ID를 추출합니다.
     *
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지당 채팅방 수 (기본값: 10)
     * @return 사용자의 채팅방 목록
     */
    @GetMapping
    public ResponseEntity<GenericResponse<Page<ChatRoomResponse>>> getChatRooms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long currentMemberId = SecurityUtil.getCurrentMemberId();

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ChatRoomResponse> chatRooms = chatRoomService.getChatRoomsForMember(currentMemberId, pageable);

        return ResponseEntity.ok(GenericResponse.ok(chatRooms, "조회를 성공했습니다."));
    }

    /**
     * 채팅방 나가기(삭제) API 엔드포인트입니다.
     * 현재 사용자가 채팅방에서 나갈 때, 상대방의 ID를 받아 채팅방 나가기 로직을 실행합니다.
     * 나간 사용자는 채팅방 참여 목록에서 제거되고, 남은 사용자에게 시스템 메시지를 전송합니다.
     *
     * @param deletingMember 현재 채팅방을 나가는 사용자 (Spring Security를 통해 주입)
     * @param otherMemberId 채팅방의 상대방 ID (요청 파라미터)
     * @return 채팅방 나가기 성공 응답
     */
    @DeleteMapping("/delete")
    public ResponseEntity<GenericResponse<Void>> deleteChatroom (
            @RequestParam("otherMemberId") Long otherMemberId) {

        // SecurityUtil 사용해서 현재 로그인한 사용자 ID 추출
        Long deletingMemberId = SecurityUtil.getCurrentMemberId();

        // MemberRepository 사용해서 Member 객체 조회
        Member deletingMember = memberRepository.findById(deletingMemberId)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.MEMBER_NOT_FOUND));

        // 상대방 멤버 조회
        Member otherMember = memberRepository.findById(otherMemberId)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.MEMBER_NOT_FOUND));

        // 채팅방 나가기 로직 실행
        chatRoomService.exitChatRoom(deletingMember, otherMember);

        // 응답 반환
        return ResponseEntity.ok(GenericResponse.ok("채팅방 나가기 성공"));
    }
}
