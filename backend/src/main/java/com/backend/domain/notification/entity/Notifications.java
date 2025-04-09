package com.backend.domain.notification.entity;

import com.backend.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Notifications {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 알림 발신자 (Member)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private Member sender;

    // 알림 수신자 (Member)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Member receiver;

    // 알림 유형 (LIKE, REQUEST, BLOCK 등)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    // 알림 메시지 (예: "{사용자명}님이 당신을 좋아합니다.")
    @Column(nullable = false)
    private String message;

    // 알림 생성 시각
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // 읽음 여부
    @Column(nullable = false)
    private boolean isRead;

    // 읽음 여부
    @Column(nullable = false)
    private boolean isDeleted;

    /**
     * 알림을 읽음 상태로 변경한다.
     */
    public void markAsRead() {
        this.isRead = true;
    }
    /**
     * 알림을 삭제 상태로 변경한다.
     */
    public void softDeleteNotification() {
        this.isDeleted = true;
    }
}
