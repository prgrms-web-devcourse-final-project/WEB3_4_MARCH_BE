package com.backend.domain.chatroom.entity;

import com.backend.domain.chatrequest.entity.ChatRequest;
import com.backend.domain.member.entity.Member;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.sql.Timestamp;
import java.time.LocalDateTime;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private Member sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Member receiver;

    @OneToOne
    @JoinColumn(name = "chat_request_id", nullable = false)
    private ChatRequest chatRequest;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createAt;

    /**
     * 채팅창에서 보낸 사람을 제외한 나머지 1명(수신자)을 찾는 메서드입니다.
     * @param currentMemberId
     * @return senderId, receiverId
     */
    public Member getAnotherMember(Member currentMemberId) {
        if (currentMemberId.equals(this.sender)) {
            return this.receiver;
        } else if (currentMemberId.equals(this.receiver)) {
            return this.sender;
        } else {
            throw new GlobalException(GlobalErrorCode.NOT_FOUND_MEMBER);
        }
    }
}
