package com.backend.domain.chatrequest.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private Long sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private Long receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatRequestStatus status; // 예: PENDING, ACCEPTED, REJECTED

    @Column(nullable = false)
    private Timestamp requestedAt;
}
