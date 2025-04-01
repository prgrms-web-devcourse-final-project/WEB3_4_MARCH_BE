package com.backend.domain.chatrequest.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRequestService {

//    private final ChatRequestRepository chatRequestRepository;
//    private final MemberRepository memberRepository;
//    private final ChatRoomRepository chatRoomRepository;
//
//    @Transactional
//    public void sendChatRequest(CustomUserDetails loginUser, Long receiverId) {
//
//        Member sender = loginUser.getMember();
//        Member receiver = memberRepository.findById(receiverId).orElseThrow(
//                () -> new GlobalException(GlobalErrorCode.NOT_FOUND_MEMBER)
//        );
//
//        validateDuplicateRequest(sender, receiver);
//
//        ChatRequest request = ChatRequest.builder()
//                .sender(sender)
//                .receiver(receiver)
//                .status(ChatRequestStatus.PENDING)
//                .requestedAt(LocalDateTime.now())
//                .build();
//
//        chatRequestRepository.save(request);
//    }
//
//    @Transactional
//    public void respondToRequest(Long requestId, boolean accept) {
//        ChatRequest request = chatRequestRepository.findById(requestId).orElseThrow(
//                () -> new GlobalException(GlobalErrorCode.NOT_FOUND_CHAT_REQUEST)
//        );
//
//        if (!request.isPending()) {
//            throw new GlobalException(GlobalErrorCode.ALREADY_PROCESSED_CHAT_REQUEST);
//        }
//
//        if (accept) {
//            request.accept();
//            createChatRoom(request);
//        } else {
//            request.reject();
//        }
//    }
//
//
//    private void createChatRoom(ChatRequest request) {
//        ChatRoom chatRoom = ChatRoom.builder()
//                .sender(request.getSender())
//                .receiver(request.getSender())
//                .createdAt(LocalDateTime.now())
//                .build();
//
//        chatRoomRepository.save(chatRoom);
//    }
//
//    private void validateDuplicateRequest(Member sender, Member receiver) {
//        if (chatRequestRepository.existsBySenderAndReceiver(sender, receiver)) {
//            throw new GlobalException(GlobalErrorCode.ALREADY_REQUESTED);
//        }
//    }
//
//    public List<ChatRequestDto> getSentRequests(CustomUserDetails loginUser) {
//        Member sender = loginUser.getMember();
//
//        return chatRequestRepository.findAllBySender(sender).stream()
//                .map(ChatRequestDto :: of)
//                .collect(Collectors.toList());
//    }
//
//    public List<ChatRequestDto> getReceivedRequests(CustomUserDetails loginUser) {
//        Member receiver = loginUser.getMember();
//
//        return chatRequestRepository.findAllByReceiver(receiver).stream()
//                .map(ChatRequestDto :: of)
//                .collect(Collectors.toList());
//    }


}
