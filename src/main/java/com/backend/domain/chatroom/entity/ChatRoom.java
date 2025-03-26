package com.backend.domain.chatroom.entity;

import com.backend.domain.chatrequest.entity.ChatRequest;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.sql.Timestamp;

public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sender_id", nullable = false)
    private ChatRequest senderId;

    @Column(name = "receiver_id", nullable = false)
    private ChatRequest receiverId;

    @Column(nullable = false)
    private Timestamp created_at;

    @Column(nullable = false)
    private boolean is_blocked;
}
