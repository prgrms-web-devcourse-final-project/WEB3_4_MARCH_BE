package com.backend.domain.chatroom.dto.response;

import com.backend.domain.chatroom.entity.ChatRoom;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 채팅방 응답 DTO입니다.
 * - 채팅방 기본 정보 및 상대방 사용자 정보를 제공합니다.
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomResponse {

    private Long id;
    private Long senderId;
    private Long receiverId;
    private LocalDateTime createdAt;

    // 상대방 정보 (현재 로그인한 사용자를 기준으로 동적으로 설정)
    private String opponentNickname;
    private String opponentProfileImgUrl;

    /**
     * ChatRoom 엔티티를 기반으로 응답 DTO를 생성합니다.
     * 현재는 상대방 정보는 빈 문자열로 초기화됩니다.
     *
     * @param chatRoom 채팅방 엔티티
     * @return ChatRoomResponse 응답 DTO
     */
    public static ChatRoomResponse from(ChatRoom chatRoom /* String nickname, String profileUrl */) {
        return ChatRoomResponse.builder()
                .id(chatRoom.getId())
                .senderId(chatRoom.getSender().getId())
                .receiverId(chatRoom.getReceiver().getId())
                .createdAt(chatRoom.getCreateAt())
                .opponentNickname("")           // TODO: 상대방 닉네임 설정 예정
                .opponentProfileImgUrl("")     // TODO: 상대방 프로필 설정 예정
                .opponentNickname("")
                .opponentProfileImgUrl("")
                .build();
    }
}
