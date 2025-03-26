package com.backend.domain.chat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
    구현되면 주석 지울 예정
     */

    // @ManyToOne
    // @JoinColumn(name = "chatroom_id")
    // private Chatroom chatRoom;

    // @ManyToOne
    // @JoinColumn(name = "sender_id")
    // private User sender;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String chatContent;

    @Column(nullable = false)
    private LocalDateTime sendTime;

    @Column(nullable = false)
    private boolean is_read;
}
