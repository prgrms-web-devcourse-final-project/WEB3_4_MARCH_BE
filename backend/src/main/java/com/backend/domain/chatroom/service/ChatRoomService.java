package com.backend.domain.chatroom.service;

import com.backend.domain.chatroom.dto.response.ChatRoomResponse;
import com.backend.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 채팅방 관련 비즈니스 로직 인터페이스입니다.
 */
public interface ChatRoomService {

    /**
     * 주어진 사용자가 참여 중인 채팅방 목록을 조회합니다.
     *
     * @param memberId 사용자 ID
     * @return 채팅방 목록
     */
    Page<ChatRoomResponse> getChatRoomsForMember(Long memberId, Pageable pageable);

    // 채팅방 삭제 (시스템 메시지 전송)
    void exitChatRoom(Member deletingMember, Member otherMember);
}
