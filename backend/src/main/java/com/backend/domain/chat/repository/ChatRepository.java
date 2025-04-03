package com.backend.domain.chat.repository;

import com.backend.domain.chat.entity.Chat;
import com.backend.domain.member.entity.Member;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    // 채팅방 번호 기준으로 보낸 시간 오름차순 정렬
    List<Chat> findByChatRoomIdOrderBySendTimeAsc(Long chatRoomId);


    // 찾은 채팅방에서 메시지를 슬라이스
    Slice<Chat> findByChatRoomId(Long chatRoomId, Pageable pageable);
}
