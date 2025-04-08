package com.backend.global.auth.payment.repository;

import com.backend.global.auth.payment.entity.BlossomTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BlossomTransactionRepository extends JpaRepository<BlossomTransaction, Long> {
    List<BlossomTransaction> findByMemberId(Long memberId);
}
