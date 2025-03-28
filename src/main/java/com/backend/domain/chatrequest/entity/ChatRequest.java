package com.backend.domain.chatrequest.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class ChatRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sender_id", nullable = false)
    private Long sender;

    @Column(name = "receiver_id", nullable = false)
    private Long receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatRequestStatus status; // 예: PENDING, ACCEPTED, REJECTED

    private Timestamp requestedAt;

    public void accept() {
        this.status = ChatRequestStatus.ACCEPTED;
    }
}
