package com.backend.domain.notification.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.domain.notification.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * 특정 사용자의 알림 목록을 생성일자 내림차순으로 조회한다.
     *
     * @param receiverId 알림 수신자 ID
     * @return 알림 목록
     */
    List<Notification> findByReceiverIdOrderByCreatedAtDesc(Long receiverId);
    
    /**
     * 특정 사용자의 알림 목록을 생성일자 내림차순으로 조회한다.
     *
     * @param receiverId 알림 수신자 ID
     * @return isDeleted가 false인 알림 목록
     */
    List<Notification> findByReceiverIdAndIsDeletedFalseOrderByCreatedAtDesc(Long receiverId);

    /**
     * 특정 사용자의 읽지 않은 알림 목록을 조회한다.
     *
     * @param receiverId 알림 수신자 ID
     * @return 읽지 않은 알림 목록
     */
    List<Notification> findByReceiverIdAndIsReadFalseOrderByCreatedAtDesc(Long receiverId);

    /**
     * 특정 사용자의 삭제되지 않고 읽지 않은 알림 목록을 조회한다.
     *
     * @param receiverId 알림 수신자 ID
     * @return 읽지 않은 알림 갯수
     */
    long countByReceiverIdAndIsReadFalseAndIsDeletedFalse(Long receiverId);
}
