package com.backend.domain.notification.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.member.entity.Member;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.domain.notification.entity.Notifications;
import com.backend.domain.notification.entity.NotificationType;
import com.backend.domain.notification.repository.NotificationRepository;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;
    /**
     * 좋아요, 대화요청, 차단 등의 이벤트 발생 시, 해당 이벤트에 대한 알림을 생성하여 저장한다.
     * <p>
     * 이 메서드는 아직 완성되지 않은 이벤트(좋아요, 대화요청, 차단)에서 호출되어,
     * 다음과 같이 사용
     * notificationService.sendNotification(receiverId, NotificationType.LIKE, senderId);
     * notificationService.sendNotification(receiverId, NotificationType.BLOCK, senderId);
     * notificationService.sendNotification(receiverId, NotificationType.REQUEST, senderId);
     * 알림 타입과 메시지를 인자로 받아 알림을 DB에 저장한다.
     * </p>
     *
     * 이벤트 발생 시 알림을 생성하여 DB에 저장한다.
     *
     * @param receiverId 알림을 받을 사용자 ID
     * @param type 이벤트 유형 (LIKE, REQUEST, BLOCK)
     * @param senderId 이벤트를 발생시킨 발신자 ID
     * @throws GlobalException 수신자 또는 발신자가 존재하지 않을 경우 예외 발생
     */
    @Transactional
    public void sendNotification(Long receiverId, NotificationType type, Long senderId) {

        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.INVALID_REQUEST));

        Member sender = memberRepository.findById(senderId)
            .orElseThrow(() -> new GlobalException(GlobalErrorCode.INVALID_REQUEST));

        String senderName = sender.getNickname();
        String message;
        switch (type) {
            case LIKE:
                message = senderName + "님이 좋아요를 눌렀습니다.";
                break;
            case REQUEST:
                message = senderName + "님이 대화 요청을 보냈습니다.";
                break;
            case BLOCK:
                message = senderName + "님이 차단되었습니다.";
                break;
            default:
                message = "새로운 알림이 도착했습니다.";
                break;
        }

        Notifications notifications = Notifications.builder()
                .receiver(receiver)
                .sender(sender)
                .type(type)
                .message(message)
                .createdAt(LocalDateTime.now(ZoneId.of("UTC")))
                .isRead(false)
                .build();
        notificationRepository.save(notifications);
    }

    /**
     * 특정 사용자의 삭제되지 않은 알림 목록을 조회한다.
     *
     * @param memberId 알림 수신자 ID
     * @return 알림 목록
     */
    @Transactional(readOnly = true)
    public List<Notifications> getNotificationsForMember(Long memberId) {
        return notificationRepository.findByReceiverIdAndIsDeletedFalseOrderByCreatedAtDesc(memberId);
    }

    /**
     * 특정 알림을 읽음 상태로 업데이트한다.
     *
     * @param notificationId 알림 ID
     */
    @Transactional
    public void markAsRead(Long notificationId) {
        Notifications notifications = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new GlobalException(GlobalErrorCode.ALREADY_REQUESTED)); // 필요에 따라 다른 예외 사용
        notifications.markAsRead();
    }

    /**
     * 특정 사용자의 읽지 않은 알림들을 모두 읽음 처리한다.
     *
     * @param memberId 알림 수신자 ID
     */
    @Transactional
    public void markAllAsRead(Long memberId) {
        List<Notifications> unreadNotifications =
            notificationRepository.findByReceiverIdAndIsReadFalseOrderByCreatedAtDesc(memberId);
        unreadNotifications.forEach(Notifications::markAsRead);
    }

    /**
     * 특정 알림을 삭제 상태로 업데이트한다.
     *
     * @param notificationId 알림 ID
     */
    @Transactional
    public void softDeleteNotification(Long notificationId) {
        Notifications notifications = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new GlobalException(GlobalErrorCode.ALREADY_REQUESTED));
        notifications.softDeleteNotification();
    }

    /**
     * 특정 사용자의 삭제되지 않은 모든 알림을 삭제 처리(soft delete)한다.
     *
     * @param memberId 알림 수신자 ID
     */
    @Transactional
    public void deleteAllNotifications(Long memberId) {
        List<Notifications> notifications =
            notificationRepository.findByReceiverIdAndIsDeletedFalseOrderByCreatedAtDesc(memberId);
        notifications.forEach(Notifications::softDeleteNotification);
    }

    /**
     * 특정 사용자의 삭제되지 않은 모든 알림을 삭제 처리(soft delete)한다.
     *
     * @param memberId 알림 수신자 ID
     * @return 남은 알림 갯수
     */
    public long getUnreadNotificationCount(Long memberId) {
        return notificationRepository.countByReceiverIdAndIsReadFalseAndIsDeletedFalse(memberId);
    }
}
