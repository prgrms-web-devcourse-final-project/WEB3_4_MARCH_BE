package com.backend.domain.notification.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.notification.dto.NotificationDto;
import com.backend.domain.notification.entity.Notification;
import com.backend.domain.notification.entity.NotificationType;
import com.backend.domain.notification.service.NotificationService;
import com.backend.global.auth.model.CustomUserDetails;
import com.backend.global.response.GenericResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/{member_id}/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    /**
     * test notification DB 생성
     */
    @PostMapping("/test")
    public String test() {

        notificationService.sendNotification(1L, NotificationType.LIKE, 2L);
        notificationService.sendNotification(1L, NotificationType.BLOCK, 3L);
        notificationService.sendNotification(2L, NotificationType.REQUEST, 3L);
        notificationService.sendNotification(2L, NotificationType.LIKE, 4L);
        notificationService.sendNotification(3L, NotificationType.BLOCK, 4L);
        notificationService.sendNotification(3L, NotificationType.REQUEST, 5L);
        notificationService.sendNotification(4L, NotificationType.LIKE, 5L);
        notificationService.sendNotification(4L, NotificationType.BLOCK, 6L);
        notificationService.sendNotification(5L, NotificationType.REQUEST, 6L);
        return "DB 등록이 완료되었습니다.";
    }

    /**
     * 특정 사용자의 알림 목록을 조회한다.
     *
     * @param memberId 알림 수신자 ID
     * @return 해당 사용자의 알림 목록
     */
    @GetMapping
    public ResponseEntity<GenericResponse<List<NotificationDto>>> getNotifications(
        @PathVariable("member_id") Long memberId,
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (!userDetails.getMemberId().equals(memberId)) {
            throw new AccessDeniedException("자신의 알림만 조회할 수 있습니다.");
        }

        List<Notification> notifications = notificationService.getNotificationsForMember(memberId);
        List<NotificationDto> dtos = notifications.stream()
                .map(NotificationDto::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(GenericResponse.of(dtos));
    }

    /**
     * 특정 알림을 읽음 상태로 업데이트한다.
     *
     * @param notificationId 알림 ID
     * @return 읽음 처리 완료 메시지
     */
    @PatchMapping("/{notification_Id}/read")
    public ResponseEntity<GenericResponse<String>> markAsRead(@PathVariable("member_id") Long memberId,@PathVariable("notification_Id") Long notificationId) {
        notificationService.markAsRead(notificationId);
        long count = notificationService.getUnreadNotificationCount(memberId);
        return ResponseEntity.ok(GenericResponse.of("알림을 읽음 처리했습니다. 남은 알림 갯수 : " + count + "개"));
    }

    /**
     * 특정 사용자의 읽지 않은 모든 알림을 읽음 처리한다.
     *
     * @param memberId 알림 수신자 ID (경로 변수 "member_id")
     * @return 모든 알림 읽음 처리 완료 메시지
     */
    @PatchMapping("/read-all")
    public ResponseEntity<GenericResponse<String>> markAllAsRead(
        @PathVariable("member_id") Long memberId) {
        notificationService.markAllAsRead(memberId);
        long count = notificationService.getUnreadNotificationCount(memberId);
        return ResponseEntity.ok(GenericResponse.of("모든 알림을 읽음 처리했습니다. 남은 알림 갯수 : " + count + "개"));
    }

    /**
     * 특정 알림을 삭제 상태로 업데이트한다.
     *
     * @param notificationId 알림 ID
     * @return 읽음 처리 완료 메시지
     */
    @PatchMapping("/{notification_Id}/delete")
    public ResponseEntity<GenericResponse<String>> softDeleteNotification(@PathVariable("member_id") Long memberId,@PathVariable("notification_Id") Long notificationId) {
        notificationService.softDeleteNotification(notificationId);
        long count = notificationService.getUnreadNotificationCount(memberId);
        return ResponseEntity.ok(GenericResponse.of("알림을 삭제했습니다. 남은 알림 갯수 : " + count + "개"));
    }

    /**
     * 특정 사용자의 삭제되지 않은 모든 알림을 삭제 처리(soft delete)한다.
     *
     * @param memberId 알림 수신자 ID (경로 변수 "member_id")
     * @return 모든 알림 삭제 처리 완료 메시지
     */
    @PatchMapping("/delete-all")
    public ResponseEntity<GenericResponse<String>> deleteAllNotifications(
        @PathVariable("member_id") Long memberId) {
        notificationService.deleteAllNotifications(memberId);
        long count = notificationService.getUnreadNotificationCount(memberId);
        return ResponseEntity.ok(GenericResponse.of("모든 알림을 삭제 처리했습니다. 남은 알림 갯수 : " + count + "개"));
    }
}
