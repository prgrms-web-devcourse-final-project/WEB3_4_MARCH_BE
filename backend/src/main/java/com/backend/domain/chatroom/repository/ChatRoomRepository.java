package com.backend.domain.chatroom.repository;

import com.backend.domain.chatroom.entity.ChatRoom;
import com.backend.domain.member.entity.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    /**
     * 두 사용자가 참여 중인 채팅방이 이미 존재하는지 확인합니다.
     * - A → B, 또는 B → A 형태의 양방향 모두 체크합니다.
     *
     * @param member1 첫 번째 사용자
     * @param member2 두 번째 사용자
     * @return 존재하는 채팅방 (없으면 Optional.empty())
     */
    Optional<ChatRoom> findBySenderAndReceiverOrReceiverAndSender(Member sender1, Member receiver1, Member sender2, Member receiver2);

    // 내가 참여 중인 채팅방(보낸 사람 or 받은 사람)을 모두 조회하는 쿼리
    // TODO : 인증 처리 완료 되면 Member 수정
    List<ChatRoom> findBySenderIdOrReceiverId(Long senderId, Long receiverId);
}
