package com.backend.domain.notification.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.member.entity.Member;
import com.backend.domain.member.exception.MemberException;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.domain.notification.entity.Notification;
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
     * 알림 타입과 메시지를 인자로 받아 알림을 DB에 저장한다.
     * </p>
     *
     * 이벤트 발생 시 알림을 생성하여 DB에 저장한다.
     *
     * @param receiverId 알림을 받을 사용자 ID
     * @param type 이벤트 유형 (LIKE, REQUEST, BLOCK)
     * @param senderId 이벤트를 발생시킨 발신자 ID
     * @throws MemberException 수신자 또는 발신자가 존재하지 않을 경우 예외 발생
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

        Notification notification = Notification.builder()
                .receiver(receiver)
                .type(type)
                .message(message)
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .build();
        notificationRepository.save(notification);
    }

    /**
     * 특정 사용자의 알림 목록을 조회한다.
     *
     * @param memberId 알림 수신자 ID
     * @return 알림 목록
     */
    public List<Notification> getNotificationsForMember(Long memberId) {
        return notificationRepository.findByReceiverIdOrderByCreatedAtDesc(memberId);
    }

    /**
     * 특정 알림을 읽음 상태로 업데이트한다.
     *
     * @param notificationId 알림 ID
     */
    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.ALREADY_REQUESTED)); // 필요에 따라 다른 예외 사용
        notification.markAsRead();
    }
}
