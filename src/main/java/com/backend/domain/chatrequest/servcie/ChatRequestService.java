//package com.backend.domain.chatrequest.servcie;
//
//import com.backend.domain.chatrequest.dto.ChatRequestResponse;
//import com.backend.domain.chatrequest.entity.ChatRequest;
//import com.backend.domain.chatrequest.entity.ChatRequestStatus;
//import com.backend.domain.chatrequest.repository.ChatRequestRepository;
//import com.backend.domain.chatroom.repository.ChatRoomRepository;
//import com.backend.global.exception.GlobalErrorCode;
//import com.backend.global.exception.GlobalException;
//import java.time.LocalDateTime;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//@RequiredArgsConstructor
//public class ChatRequestService {
//    private final ChatRequestRepository chatRequestRepository;
//    // private final MemberRepository memberRepository;
//    private final ChatRoomRepository chatRoomRepository;
//
//    /**
//     * -- 채팅 요청 생성 메소드 --
//     * 요청 사용자와 받은 사용자 ID를 기반으로 새로운 채팅 요청을 생성하는 메소드
//     *
//     * @param senderId 요청 사용자 ID
//     * @param receiverId 요청 받은 사용자 ID
//     * @return ChatRequestResponseDto
//     *
//     * @author -- 김현곤 --
//     * @since -- 3월 26일 --
//     */
//    @Transactional
//    public ChatRequestResponse createRequest(Long senderId, Long receiverId) {
//
//        ChatRequest sender = chatRequestRepository.findById(senderId)
//                .orElseThrow(() -> new GlobalException(GlobalErrorCode.CHAT_NOT_FOUND_BY_ID));
//        ChatRequest receiver = chatRequestRepository.findById(receiverId)
//                .orElseThrow(() -> new GlobalException(GlobalErrorCode.CHAT_NOT_FOUND_BY_ID));
//
//        if (chatRequestRepository.existsBySenderIdAndReceiverId(senderId, receiverId)) {
//            throw new GlobalException(GlobalErrorCode.DUPLICATE_CHAT_REQUEST);
//        }
//
//        ChatRequest request = ChatRequest.builder()
//                .sender(sender)
//                .receiver(receiver)
//                .status(ChatRequestStatus.PENDING)
//                .requestedAt(LocalDateTime.now())
//                .build();
//
//        ChatRequest saved = chatRequestRepository.save(request);
//
//        return ChatRequestResponse.from(saved);
//    }
//}
