package com.backend.domain.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.backend.domain.chat.entity.Chat;
import com.backend.domain.chat.repository.ChatRepository;
import com.backend.domain.chat.service.redis.RedisPublisher;
import com.backend.domain.chat.service.redis.RedisUnreadService;
import com.backend.domain.chatroom.dto.response.ChatRoomResponse;
import com.backend.domain.chatroom.entity.ChatRoom;
import com.backend.domain.chatroom.repository.ChatRoomRepository;
import com.backend.domain.chatroom.service.ChatRoomServiceImpl;
import com.backend.domain.member.entity.Member;
import com.backend.global.auth.model.CustomUserDetails;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

class ChatRoomServiceTest {

    @InjectMocks
    private ChatRoomServiceImpl chatRoomService;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private RedisUnreadService redisUnreadService;

    @Mock
    private RedisPublisher redisPublisher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // 테스트용 인증 정보를 SecurityContextHolder에 설정합니다.
        CustomUserDetails dummyUser = new CustomUserDetails(
                1L,
                "dummy@example.com",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                dummyUser,
                "dummy-token",
                dummyUser.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void testGetChatRoomsForMember() {
        // 테스트용 현재 로그인 사용자 ID = 1L
        Long currentMemberId = 1L;

        // 더미 회원 생성: sender가 현재 로그인한 사용자, receiver가 상대방 "수진"
        Member sender = Member.builder()
                .id(1L)
                .kakaoId(1001L)
                .nickname("Sender")
                .build();
        Member receiver = Member.builder()
                .id(2L)
                .kakaoId(2002L)
                .nickname("수진")
                .build();

        // 더미 채팅방 생성: 현재 사용자가 sender인 경우, 상대방은 receiver
        ChatRoom chatRoom = ChatRoom.builder()
                .id(10L)
                .sender(sender)
                .receiver(receiver)
                .createAt(LocalDateTime.now())
                .build();

        List<ChatRoom> chatRoomList = new ArrayList<>();
        chatRoomList.add(chatRoom);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<ChatRoom> chatRoomPage = new PageImpl<>(chatRoomList, pageable, chatRoomList.size());

        // 모킹: 채팅방 레포지토리
        when(chatRoomRepository.findAllWithMembers(currentMemberId, pageable))
                .thenReturn(chatRoomPage);
        // 모킹: RedisUnreadService에서 읽지 않은 메시지 개수 반환(예: 0)
        when(redisUnreadService.getUnreadCount(currentMemberId, chatRoom.getId()))
                .thenReturn(0);

        // 모킹: 채팅 레포지토리에서 최신 메시지 조회
        // 최신 메시지가 존재한다고 가정
        Chat lastChat = Chat.builder()
                .chatRoom(chatRoom)
                .sender(sender) // sender가 보낸 메시지
                .chatContent("네, 내일 3시에 카페에서 만나요! 기대되네요 😊")
                .sendTime(LocalDateTime.of(2025, 4, 14, 22, 23))
                .isRead(true)
                .build();
        when(chatRepository.findFirstByChatRoomIdOrderBySendTimeDesc(chatRoom.getId()))
                .thenReturn(Optional.of(lastChat));

        // 서비스 메서드 호출
        Page<ChatRoomResponse> responsePage = chatRoomService.getChatRoomsForMember(currentMemberId, pageable);

        // 검증
        assertNotNull(responsePage);
        assertEquals(1, responsePage.getTotalElements());
        ChatRoomResponse response = responsePage.getContent().get(0);
        // 현재 사용자가 sender이면, 상대방(= opponent)이 receiver여야 함.
        // 확인: opponent 정보 (중첩 DTO) 설정이 올바른지.
        assertNotNull(response.getOpponent());
        assertEquals("수진", response.getOpponent().getName());
        // 마지막 메시지 정보 검증
        assertNotNull(response.getMessage());
        assertEquals("네, 내일 3시에 카페에서 만나요! 기대되네요 😊", response.getMessage().getText());
        // 읽지 않은 메시지 개수 검증
        assertEquals(0, response.getUnreadCount());
    }

    @Test
    void testExitChatRoom_success() {
        // 더미 데이터 생성: 삭제할 회원(deletingMember)과 상대방(otherMember)
        Member deletingMember = Member.builder()
                .id(1L)
                .nickname("Sender")
                .build();
        Member otherMember = Member.builder()
                .id(2L)
                .nickname("수진")
                .build();

        // 채팅방 생성: 두 회원 참여
        ChatRoom chatRoom = ChatRoom.builder()
                .id(10L)
                .sender(deletingMember)
                .receiver(otherMember)
                .createAt(LocalDateTime.now())
                .build();

        // 모킹: 채팅방 레포지토리에서 채팅방 조회
        when(chatRoomRepository.findBySenderAndReceiverOrReceiverAndSender(
                deletingMember, otherMember, otherMember, deletingMember))
                .thenReturn(Optional.of(chatRoom));

        // exitChatRoom 호출
        chatRoomService.exitChatRoom(deletingMember, otherMember);

        // 검증: 채팅방 저장 호출 (즉, 채팅방에서 deletingMember 제거 후 저장)
        verify(chatRoomRepository, times(1)).save(chatRoom);
        // 검증: RedisPublisher.publish 호출되어 시스템 메시지가 전송되는지 확인
        verify(redisPublisher, times(1)).publish(any());
    }

    @Test
    void testExitChatRoom_notFound() {
        Member member1 = Member.builder().id(1L).nickname("Sender").build();
        Member member2 = Member.builder().id(2L).nickname("수진").build();

        // 채팅방이 존재하지 않는 상황 모킹
        when(chatRoomRepository.findBySenderAndReceiverOrReceiverAndSender(
                member1, member2, member2, member1))
                .thenReturn(Optional.empty());

        // exitChatRoom 호출 시 예외 발생 확인
        GlobalException exception = assertThrows(GlobalException.class, () ->
                chatRoomService.exitChatRoom(member1, member2)
        );
        assertEquals(GlobalErrorCode.CHATROOM_NOT_FOUND, "CHATROOM_NOT_FOUND");
    }
}
