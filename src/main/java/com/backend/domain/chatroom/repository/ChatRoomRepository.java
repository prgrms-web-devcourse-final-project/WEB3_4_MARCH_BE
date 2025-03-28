package com.backend.domain.chatroom.repository;

import com.backend.domain.chatroom.entity.ChatRoom;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    /**
     * 양방향 체크 로직
     * @param senderId
     * @param receiverId
     * @return
     */
    @Query("SELECT r FROM ChatRoom r WHERE " +
            "(r.senderId = :senderId AND r.receiverId = :receiverId) OR " +
            "(r.senderId = :receiverId AND r.receiverId = :senderId)")
    Optional<ChatRoom> findRoomByMembers(Long senderId, Long receiverId);
}
