package com.backend.domain.chatrequest.entity;

import com.backend.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "chat_request")
public class ChatRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private Member sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Member receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatRequestStatus status;

    @Column(nullable = false)
    private LocalDateTime requestedAt;

    public boolean isPending() {
        return this.status == ChatRequestStatus.PENDING;
    }

    public void accept() {
        this.status = ChatRequestStatus.ACCEPTED;
    }

    public void reject() {
        this.status = ChatRequestStatus.REJECTED;
    }

}
