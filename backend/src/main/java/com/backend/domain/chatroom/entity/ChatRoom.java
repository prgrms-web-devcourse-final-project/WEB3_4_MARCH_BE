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
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
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
     * 채팅창에서 현재 참여자(currentMemberId)를 제외한 다른 참여자(수신자)를 찾는 메서드입니다.
     *
     * @param currentMember 현재 채팅창의 참여자 (삭제 요청자 포함)
     * @return 현재 참여자가 아닌 다른 참여자 (sender 또는 receiver)
     * @throws GlobalException 만약 currentMemberId가 sender나 receiver에 해당하지 않는 경우 예외를 발생시킵니다.
     */
    public Member getAnotherMember(Member currentMember) {

        log.info("currentMember id: {}", currentMember.getId());
        log.info("this.sender id: {}", this.sender.getId());
        log.info("this.receiver id: {}", this.receiver.getId());

        if (currentMember.equals(this.sender)) {
            log.info("this.receiver id: {}", this.receiver.getId());
            // 현재 참여자가 sender라면, 상대방은 receiver
            return this.receiver;

        } else if (currentMember.equals(this.receiver)) {
            log.info("this.sender id: {}", this.sender.getId());
            // 현재 참여자가 receiver라면, 상대방은 sender
            return this.sender;

        } else {
            log.error("currentMember does not match sender or receiver. currentMember: {}, sender: {}, receiver: {}",
                    currentMember, this.sender, this.receiver);
            // currentMemberId가 sender 또는 receiver가 아닌 경우, 에러 발생
            throw new GlobalException(GlobalErrorCode.MEMBER_NOT_FOUND);
        }
    }

    /**
     * 채팅방에서 특정 사용자를 제거하고, 제거되지 않은 다른 참여자를 반환하는 메서드입니다.
     * 제거 요청한 사용자가 채팅방에 존재하는지 확인 후, 해당 필드를 null로 설정합니다.
     *
     * @param deletingMember 채팅방을 나가는 사용자
     * @return 채팅방에 남아있는 다른 참여자
     */
    public Member removeParticipant(Member deletingMember) {
        // 삭제하기 전 현재 삭제할 사용자가 채팅방의 참여자인지 확인
        Member otherMember = getAnotherMember(deletingMember);

        // 삭제할 사용자가 sender라면 sender == null, receiver라면 receiver == null
        if (deletingMember.equals(this.sender)) {
            this.sender = null;
        } else if (deletingMember.equals(this.receiver)) {
            this.receiver = null;
        }

        return otherMember;
    }

    /**
     * 주어진 멤버가 채팅방의 참여자(sender 또는 receiver)인지 확인합니다.
     *
     * @param otherMember 확인할 멤버
     * @return 멤버가 참여자이면 true, 아니면 false
     */
    public boolean hasParticipant(Member otherMember) {
        return (this.sender != null && this.sender.equals(otherMember))
                || (this.receiver != null && this.receiver.equals(otherMember));
    }
}
