package com.backend.domain.chatroom.dto.response;

import com.backend.domain.chatroom.entity.ChatRoom;
import com.backend.domain.member.entity.Member;
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
    private int unreadCount;

    // 상대방 정보 (현재 로그인한 사용자를 기준으로 동적으로 설정)
    private String opponentNickname;
    private String opponentProfileImgUrl;

    /**
     * ChatRoom 엔티티를 기반으로 응답 DTO를 생성합니다.
     * - 현재 로그인한 사용자의 ID를 기준으로 상대방 정보를 추출하여 포함합니다.
     * - 상대방 닉네임(opponentNickname), 프로필 이미지(opponentProfileImgUrl)를 함께 반환합니다.
     *
     * @param chatRoom        채팅방 엔티티
     * @param currentMemberId 현재 로그인한 사용자 ID
     * @return ChatRoomResponse 응답 DTO
     */
    public static ChatRoomResponse from(ChatRoom chatRoom, Long currentMemberId, int unreadCount) {
        Member opponent = chatRoom.getSender().getId().equals(currentMemberId)
                ? chatRoom.getReceiver()
                : chatRoom.getSender();

        return ChatRoomResponse.builder()
                .id(chatRoom.getId())
                .senderId(chatRoom.getSender().getId())
                .receiverId(chatRoom.getReceiver().getId())
                .createdAt(chatRoom.getCreateAt())
                .opponentNickname(opponent.getNickname())
                .opponentProfileImgUrl(opponent.getProfileImage() != null ? opponent.getProfileImage().getUrl() : null)
                .unreadCount(unreadCount)
                .build();
    }
}
