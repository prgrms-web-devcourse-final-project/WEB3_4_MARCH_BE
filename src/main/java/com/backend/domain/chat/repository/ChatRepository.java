package com.backend.domain.chat.repository;

import com.backend.domain.chat.entity.Chat;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    // 채팅방 번호 기준으로 보낸 시간 오름차순 정렬
    List<Chat> findByChatRoomIdOrderBySendTimeAsc(Long chatRoomId);

    // 채팅방 번호 기준으로 읽지 않은 메시지 카운트
    // TODO : 추후에 memberId로 변경
    @Query("SELECT COUNT(c) "
            + "FROM Chat c "
            + "WHERE c.chatRoom.id = :chatRoomId "
            + "AND c.isRead = false "
            + "AND c.sender <> :receiverId")
    int countUnreadMessages(@Param("chatRoomId") Long chatRoomId, @Param("receiverId") Long receiverId);
}
