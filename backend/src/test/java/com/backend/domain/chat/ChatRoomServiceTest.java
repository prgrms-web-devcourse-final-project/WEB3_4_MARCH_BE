package com.backend.domain.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.backend.domain.chat.repository.ChatRepository;
import com.backend.domain.chat.service.redis.RedisPublisher;
import com.backend.domain.chat.service.redis.RedisUnreadService;
import com.backend.domain.chatroom.dto.response.ChatRoomResponse;
import com.backend.domain.chatroom.entity.ChatRoom;
import com.backend.domain.chatroom.repository.ChatRoomRepository;
import com.backend.domain.chatroom.service.ChatRoomServiceImpl;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.entity.Role;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

class ChatRoomServiceTest {

    @InjectMocks
    private ChatRoomServiceImpl chatRoomService;

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private RedisUnreadService redisUnreadService;

    @Mock
    private RedisPublisher redisPublisher;

    @Mock
    private MemberRepository memberRepository;

    private Member sender;
    private Member receiver;
    private ChatRoom chatRoom;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sender = Member.builder().id(1L).nickname("홍길동").role(Role.ROLE_USER).build();
        receiver = Member.builder().id(2L).nickname("수진").role(Role.ROLE_USER).build();

        chatRoom = ChatRoom.builder()
                .id(100L)
                .sender(sender)
                .receiver(receiver)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testGetChatRoomsForMember() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<ChatRoom> chatRoomPage = new PageImpl<>(List.of(chatRoom));

        when(memberRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(chatRoomRepository.findAllWithMembers(1L, pageable)).thenReturn(chatRoomPage);
        when(redisUnreadService.getUnreadCount(1L, 100L)).thenReturn(0);
        when(chatRepository.findFirstByChatRoomIdOrderBySendTimeDesc(100L)).thenReturn(Optional.empty());

        Page<ChatRoomResponse> response = chatRoomService.getChatRoomsForMember(1L, pageable);

        assertEquals(1, response.getTotalElements());
        assertEquals("수진", response.getContent().get(0).getOpponent().getName());
    }

    @Test
    void testGetChatRoomsForTempUser_ThrowsException() {
        Member sender = Member.builder()
                .id(1L)
                .kakaoId(1001L)
                .nickname("테스트 유저")
                .role(Role.ROLE_TEMP_USER)
                .build();
        when(memberRepository.findById(1L)).thenReturn(Optional.of(sender));

        GlobalException exception = assertThrows(GlobalException.class, () ->
                chatRoomService.getChatRoomsForMember(1L, PageRequest.of(0, 5)));

        assertEquals(GlobalErrorCode.CHATROOM_FORBIDDEN, exception.getGlobalErrorCode());
    }

    @Test
    void testExitChatRoom_success() {
        when(chatRoomRepository.findBySenderAndReceiverOrReceiverAndSender(any(), any(), any(), any()))
                .thenReturn(Optional.of(chatRoom));

        chatRoomService.exitChatRoom(sender, receiver);

        verify(chatRoomRepository).save(any(ChatRoom.class));
        verify(redisPublisher).publish(any());
    }

    @Test
    void testExitChatRoom_notFound() {
        when(chatRoomRepository.findBySenderAndReceiverOrReceiverAndSender(any(), any(), any(), any()))
                .thenReturn(Optional.empty());

        GlobalException exception = assertThrows(GlobalException.class, () ->
                chatRoomService.exitChatRoom(sender, receiver));

        assertEquals(GlobalErrorCode.CHATROOM_NOT_FOUND, exception.getGlobalErrorCode());
    }
}