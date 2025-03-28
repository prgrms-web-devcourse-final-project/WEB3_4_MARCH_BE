package com.backend.domain.chatrequest.service;

import com.backend.domain.chat.exception.ChatErrorCode;
import com.backend.domain.chat.exception.ChatException;
import com.backend.domain.chatrequest.dto.ChatRequestRequest;
import com.backend.domain.chatrequest.dto.ChatRequestResponse;
import com.backend.domain.chatrequest.entity.ChatRequest;
import com.backend.domain.chatrequest.entity.ChatRequestStatus;
import com.backend.domain.chatrequest.repository.ChatRequestRepository;
import com.backend.domain.chatroom.dto.ChatRoomResponse;
import com.backend.domain.chatroom.entity.ChatRoom;
import com.backend.domain.chatroom.repository.ChatRoomRepository;
import java.sql.Timestamp;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatRequestServiceImpl implements ChatRequestService {

    private final ChatRequestRepository chatRequestRepository;
    private final ChatRoomRepository chatRoomRepository;

    /** -- 테스트용 요청 만드는 메서드 --
     *
     * @param chatRequest
     */
    @Override
    public ChatRequestResponse createRequest(ChatRequestRequest chatRequest) {

        Long senderId = chatRequest.getSenderId();
        Long receiverId = chatRequest.getReceiverId();

        if (chatRequestRepository.existsBySenderAndReceiver(senderId, receiverId)) {
            throw new ChatException(ChatErrorCode.DUPLICATE_CHAT_REQUEST);
        }

        ChatRequest request = ChatRequest.builder()
                .sender(senderId)
                .receiver(receiverId)
                .status(ChatRequestStatus.PENDING)
                .requestedAt(Timestamp.from(Instant.now()))
                .build();

        ChatRequest saved = chatRequestRepository.save(request);

        return ChatRequestResponse.from(saved);
    }

    /**
     * -- 테스트용 채팅 요청 수락 메소드 --
     * 채팅 요청 ID를 기반으로 요청을 수락하고 채팅방을 생성하는 메소드
     *
     * @param requestId 채팅 요청 ID
     *
     * @author -- 김현곤 --
     * @since -- 3월 27일 --
     */
    @Transactional
    public ChatRoomResponse acceptRequest(Long requestId) {
        ChatRequest request = chatRequestRepository.findById(requestId)
                .orElseThrow(() -> new ChatException(ChatErrorCode.NOT_FOUND_BY_REQUEST));

        request.accept();
        chatRequestRepository.save(request);

        ChatRoom room = ChatRoom.builder()
                .senderId(request.getSender())
                .receiverId(request.getReceiver())
                .chatRequest(request)
                .is_blocked(false)
                .createdAt(Timestamp.from(Instant.now()))
                .build();

        ChatRoom saved =  chatRoomRepository.save(room);
        return ChatRoomResponse.from(saved);
    }
}
