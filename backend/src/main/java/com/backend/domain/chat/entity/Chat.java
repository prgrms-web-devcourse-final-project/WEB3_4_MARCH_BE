package com.backend.domain.chat.entity;

import com.backend.domain.chatroom.entity.ChatRoom;
import com.backend.domain.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@Table(name = "Chat")
@AllArgsConstructor()
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private Member sender;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String chatContent;

    @Column(nullable = false)
    private LocalDateTime sendTime;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    public Chat(ChatRoom room, Member sender, String content, LocalDateTime sendTime) {
        this.chatRoom = room;
        this.sender = sender;
        this.chatContent = content;
        this.sendTime = sendTime;
    }
}

