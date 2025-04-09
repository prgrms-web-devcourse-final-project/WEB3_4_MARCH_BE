package com.backend.domain.notification.dto;

import java.time.LocalDateTime;

import com.backend.domain.notification.entity.Notifications;
import com.backend.domain.notification.entity.NotificationType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 알림 정보를 클라이언트에 전달하기 위한 DTO 클래스.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    
    private Long id;                 // 알림 ID
    private Long senderId;         // 알림 수신자 ID
    private Long receiverId;         // 알림 수신자 ID
    private NotificationType type;   // 알림 유형 (LIKE, REQUEST, BLOCK 등)
    private String message;          // 알림 메시지
    private LocalDateTime createdAt; // 알림 생성 시각
    private boolean isRead;          // 읽음 여부
    private boolean isDeleted;          // 읽음 여부

    /**
     * Notification 엔티티를 NotificationDto로 변환한다.
     *
     * @param notifications 알림 엔티티
     * @return 변환된 NotificationDto 객체
     */
    public static NotificationDto from(Notifications notifications) {
        return new NotificationDto(
            notifications.getId(),
            notifications.getSender().getId(),
            notifications.getReceiver().getId(),
            notifications.getType(),
            notifications.getMessage(),
            notifications.getCreatedAt(),
            notifications.isRead(),
            notifications.isDeleted()
        );
    }
}
