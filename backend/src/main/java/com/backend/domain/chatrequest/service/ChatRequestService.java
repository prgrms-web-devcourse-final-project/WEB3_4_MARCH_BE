package com.backend.domain.chatrequest.service;

import com.backend.domain.chatrequest.dto.response.ChatRequestDto;
import com.backend.domain.chatrequest.entity.ChatRequest;
import com.backend.domain.chatrequest.entity.ChatRequestStatus;
import com.backend.domain.chatrequest.repository.ChatRequestRepository;
import com.backend.domain.chatroom.entity.ChatRoom;
import com.backend.domain.chatroom.repository.ChatRoomRepository;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.domain.notification.entity.NotificationType;
import com.backend.domain.notification.service.NotificationService;
import com.backend.global.auth.model.CustomUserDetails;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRequestService {

    private final ChatRequestRepository chatRequestRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final NotificationService notificationService;

    @Transactional
    public void sendChatRequest(CustomUserDetails loginUser, Long receiverId) {

        Member sender = memberRepository.findById(loginUser.getMemberId()).orElseThrow(
                () -> new GlobalException(GlobalErrorCode.MEMBER_NOT_FOUND)
        );
        Member receiver = memberRepository.findById(receiverId).orElseThrow(
                () -> new GlobalException(GlobalErrorCode.MEMBER_NOT_FOUND)
        );

        validateDuplicateRequest(sender, receiver);

        ChatRequest request = ChatRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .status(ChatRequestStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .build();

        // 알림설정
        notificationService.sendNotification(receiverId, NotificationType.REQUEST, loginUser.getMemberId());

        chatRequestRepository.save(request);
    }

    @Transactional
    public void respondToRequest(Long requestId, boolean accept) {
        ChatRequest request = chatRequestRepository.findById(requestId).orElseThrow(
                () -> new GlobalException(GlobalErrorCode.NOT_FOUND_CHAT_REQUEST)
        );

        if (!request.isPending()) {
            throw new GlobalException(GlobalErrorCode.ALREADY_PROCESSED_CHAT_REQUEST);
        }

        if (accept) {
            request.accept();
            createChatRoom(request);
        } else {
            request.reject();
        }
    }


    private void createChatRoom(ChatRequest request) {
        ChatRoom chatRoom = ChatRoom.builder()
                .sender(request.getSender())
                .receiver(request.getSender())
                .createAt(LocalDateTime.now())
                .build();

        chatRoomRepository.save(chatRoom);
    }

    private void validateDuplicateRequest(Member sender, Member receiver) {
        if (chatRequestRepository.existsBySenderAndReceiver(sender, receiver)) {
            throw new GlobalException(GlobalErrorCode.ALREADY_REQUESTED);
        }
    }

    public List<ChatRequestDto> getSentRequests(CustomUserDetails loginUser) {
        Member sender = memberRepository.findById(loginUser.getMemberId()).orElseThrow(
                () -> new GlobalException(GlobalErrorCode.MEMBER_NOT_FOUND)
        );

        return chatRequestRepository.findAllBySender(sender).stream()
                .map(ChatRequestDto :: of)
                .collect(Collectors.toList());
    }

    public List<ChatRequestDto> getReceivedRequests(CustomUserDetails loginUser) {

        Member receiver = memberRepository.findById(loginUser.getMemberId()).orElseThrow(
                () -> new GlobalException(GlobalErrorCode.MEMBER_NOT_FOUND)
        );

        return chatRequestRepository.findAllByReceiver(receiver).stream()
                .map(ChatRequestDto:: of)
                .collect(Collectors.toList());
    }


}
