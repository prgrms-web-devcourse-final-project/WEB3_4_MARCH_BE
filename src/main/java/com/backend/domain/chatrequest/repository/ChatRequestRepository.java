package com.backend.domain.chatrequest.repository;

import com.backend.domain.chatrequest.entity.ChatRequest;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRequestRepository extends JpaRepository<ChatRequest, Long> {
    boolean existsBySenderAndReceiver(Long sender, Long receiver);

    @Query("SELECT cr "
            + "FROM ChatRequest cr "
            + "WHERE (cr.sender = :senderId AND cr.receiver = :receiverId)"
            + " OR (cr.sender = :receiverId AND cr.receiver = :senderId)")
    Optional<ChatRequest> findRequestByMembers(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);
}
