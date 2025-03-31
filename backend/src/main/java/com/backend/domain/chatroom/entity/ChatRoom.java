package com.backend.domain.chatroom.entity;

import com.backend.domain.chatrequest.entity.ChatRequest;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;

    @OneToOne
    @JoinColumn(name = "chat_request_id", nullable = false)
    private ChatRequest chatRequest;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @Column(nullable = false)
    private boolean is_blocked;

    /**
     * 차단 검증
     */
    public void validateBlocked() {
        if (this.is_blocked) {
            throw new GlobalException(GlobalErrorCode.BLOCKED_MEMBER);
        }
    }

    /**
     * 채팅창에서 보낸 사람을 제외한 나머지 1명(수신자)을 찾는 메서드입니다.
     * @param currentMemberId
     * @return senderId, receiverId
     */
    public Long getAnotherUserId(Long currentMemberId) {
        if (currentMemberId.equals(this.senderId)) {
            return this.receiverId;
        } else if (currentMemberId.equals(this.receiverId)) {
            return this.senderId;
        } else {
            throw new GlobalException(GlobalErrorCode.NOT_FOUND_BY_ID);
        }
    }
}
