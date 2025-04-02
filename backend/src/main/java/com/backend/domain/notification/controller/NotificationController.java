package com.backend.domain.notification.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.notification.dto.NotificationDto;
import com.backend.domain.notification.entity.Notification;
import com.backend.domain.notification.service.NotificationService;
import com.backend.global.response.GenericResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/{member_id}/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 특정 사용자의 알림 목록을 조회한다.
     *
     * @param memberId 알림 수신자 ID
     * @return 해당 사용자의 알림 목록
     */
    @GetMapping
    public ResponseEntity<GenericResponse<List<NotificationDto>>> getNotifications(@PathVariable("member_id") Long memberId) {

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
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<GenericResponse<String>> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok(GenericResponse.of("알림을 읽음 처리했습니다."));
    }
}
