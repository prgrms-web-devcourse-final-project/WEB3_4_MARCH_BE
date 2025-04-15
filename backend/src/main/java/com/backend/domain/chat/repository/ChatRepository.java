package com.backend.domain.chat.repository;

import com.backend.domain.chat.entity.Chat;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    // 채팅방 번호 기준으로 보낸 시간 오름차순 정렬
    List<Chat> findByChatRoomIdOrderBySendTimeAsc(Long chatRoomId);

    // 채팅방 번호 기준 마지막으로 보낸 메시지
    Optional<Chat> findFirstByChatRoomIdOrderBySendTimeDesc(Long chatRoomId);

    // UUID 찾는 메서드
    Optional<Chat> findByUuid(String uuid);

    // UUID가 이미 있는지 검증하는 메서드
    boolean existsByUuid(String uuid);

    // 찾은 채팅방에서 메시지를 슬라이스
    Slice<Chat> findByChatRoomId(Long chatRoomId, Pageable pageable);

    // 읽음 업데이트
    @Modifying
    @Query("UPDATE Chat c SET c.isRead = true WHERE c.chatRoom.id = :roomId AND c.chatRoom.receiver.id = :memberId AND c.isRead = false")
    int updateMessagesToRead(@Param("roomId") Long roomId, @Param("memberId") Long memberId);
}
