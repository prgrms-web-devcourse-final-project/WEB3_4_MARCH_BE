package com.backend.domain.chatrequest.repository;

import com.backend.domain.chatrequest.entity.ChatRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRequestRepository extends JpaRepository<ChatRequest, Long> {
    boolean existsBySenderIdAndReceiverId(Long senderId, Long receiverId);
}
