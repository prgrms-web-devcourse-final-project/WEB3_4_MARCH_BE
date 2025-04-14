package com.backend.domain.chatrequest;

import com.backend.domain.chatrequest.entity.ChatRequest;
import com.backend.domain.chatrequest.repository.ChatRequestRepository;
import com.backend.domain.chatrequest.service.ChatRequestService;
import com.backend.domain.chatroom.repository.ChatRoomRepository;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.domain.notification.entity.NotificationType;
import com.backend.domain.notification.service.NotificationService;
import com.backend.global.auth.model.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class ChatRequestServiceTest {

    private ChatRequestRepository chatRequestRepository;
    private MemberRepository memberRepository;
    private ChatRoomRepository chatRoomRepository;
    private NotificationService notificationService;

    private ChatRequestService chatRequestService;

    private Member sender;
    private Member receiver;
    private CustomUserDetails loginUser;

    @BeforeEach
    void setUp() {

        // 모든 의존성을 Mock 객체로 초기화
        chatRequestRepository = mock(ChatRequestRepository.class);
        memberRepository = mock(MemberRepository.class);
        chatRoomRepository = mock(ChatRoomRepository.class);
        notificationService = mock(NotificationService.class);

        chatRequestService = new ChatRequestService(
                chatRequestRepository,
                memberRepository,
                chatRoomRepository,
                notificationService
        );

        sender = Member.builder().id(1L).build();
        receiver = Member.builder().id(2L).build();

        loginUser = new CustomUserDetails(sender.getId(), "dummy@email.com", List.of());
    }

    @Test
    @DisplayName("채팅 요청 성공")
    void sendChatRequest_success() {

        // given
        when(memberRepository.findById(sender.getId())).thenReturn(Optional.of(sender));
        when(memberRepository.findById(receiver.getId())).thenReturn(Optional.of(receiver));
        when(chatRequestRepository.existsBySenderAndReceiver(sender, receiver)).thenReturn(false);

        // when
        chatRequestService.sendChatRequest(loginUser, receiver.getId());

        // then
        verify(chatRequestRepository).save(any(ChatRequest.class));
        verify(notificationService).sendNotification(
                eq(receiver.getId()),
                eq(NotificationType.REQUEST),
                eq(sender.getId()));
    }


}
