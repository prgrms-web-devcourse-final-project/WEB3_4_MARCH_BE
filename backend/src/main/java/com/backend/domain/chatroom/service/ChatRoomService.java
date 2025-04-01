package com.backend.domain.chatroom.service;

import com.backend.domain.chatroom.dto.response.ChatRoomResponse;
import com.backend.domain.member.entity.Member;
import java.util.List;

/**
 * 채팅방 관련 비즈니스 로직 인터페이스입니다.
 */
public interface ChatRoomService {

    /**
     * 채팅방을 생성합니다.
     * - 중복 채팅방이 존재하지 않아야 하며,
     * - 기존에 존재하는 ChatRequest를 기반으로 채팅방이 생성됩니다.
     *
     * @param senderId 채팅 요청을 보낸 사용자
     * @param receiverId 채팅 요청을 받은 사용자
     * @return 생성된 채팅방 응답 DTO
     */
    ChatRoomResponse createChatRoom(Member senderId, Member receiverId);

    /**
     * 주어진 사용자가 참여 중인 채팅방 목록을 조회합니다.
     *
     * @param memberId 사용자 ID
     * @return 채팅방 목록
     */
    List<ChatRoomResponse> getChatRoomsForMember(Long memberId);
}
