package com.backend.domain.notification.entity;

import java.time.LocalDateTime;

import com.backend.domain.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    /**
     * 알림을 읽음 상태로 변경한다.
     */
    public void markAsRead() {
        this.isRead = true;
    }
}
