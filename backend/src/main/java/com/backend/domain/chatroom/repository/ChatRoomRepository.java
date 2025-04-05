package com.backend.domain.chatroom.repository;

import com.backend.domain.chatroom.entity.ChatRoom;
import com.backend.domain.member.entity.Member;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    /**
     * 두 사용자가 참여 중인 채팅방이 이미 존재하는지 확인합니다.
     * - A -> B 또는 B -> A 형태의 양방향 모두를 체크합니다.
     * - 예: (sender = A, receiver = B) OR (sender = B, receiver = A)
     *
     * @param sender1 첫 번째 경우의 sender
     * @param receiver1 첫 번째 경우의 receiver
     * @param sender2 두 번째 경우의 sender (역방향)
     * @param receiver2 두 번째 경우의 receiver (역방향)
     * @return 존재하는 채팅방이 있다면 Optional로 반환, 없으면 Optional.empty()
     */
    Optional<ChatRoom> findBySenderAndReceiverOrReceiverAndSender(
            Member sender1, Member receiver1,
            Member sender2, Member receiver2
    );

    /**
     * 특정 사용자가 참여 중인 채팅방 목록을 페이징하여 조회합니다.
     * - sender 또는 receiver가 해당 사용자일 경우 모두 포함됩니다.
     * - sender, receiver를 fetch join 하여 Lazy 로딩 예외를 방지합니다.
     * - countQuery를 별도로 지정하여 페이지 계산이 가능하도록 합니다.
     *
     * @param memberId 조회할 사용자 ID
     * @param pageable 페이징 정보
     * @return 사용자가 참여 중인 채팅방 페이지 객체
     */
    @Query("SELECT DISTINCT cr FROM ChatRoom cr " +
            "JOIN FETCH cr.sender " +
            "JOIN FETCH cr.receiver " +
            "WHERE cr.id = :chatRoomId")
    Page<ChatRoom> findAllWithMembers(@Param("memberId") Long memberId, Pageable pageable);
}
