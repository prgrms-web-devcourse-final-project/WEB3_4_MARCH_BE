package com.backend.global.auth.payment.repository;

import com.backend.global.auth.payment.entity.BlossomTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BlossomTransactionRepository extends JpaRepository<BlossomTransaction, Long> {
    List<BlossomTransaction> findByMemberId(Long memberId);

    Optional<BlossomTransaction> findByTossOrderId(String tossOrderId);

    Optional<BlossomTransaction> findByPaymentKey(String paymentKey);
}