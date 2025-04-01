package com.backend.domain.chatrequest.repository;

import com.backend.domain.chatrequest.entity.ChatRequest;
import com.backend.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRequestRepository extends JpaRepository<ChatRequest, Long> {

    boolean existsBySenderAndReceiver(Member sender, Member receiver);

    List<ChatRequest> findAllBySender(Member sender);

    List<ChatRequest> findAllByReceiver(Member receiver);

}


