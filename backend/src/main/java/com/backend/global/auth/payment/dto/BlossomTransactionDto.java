package com.backend.global.auth.payment.dto;

import java.time.LocalDateTime;

public record BlossomTransactionDto(
        Long id,
        Long memberId,
        Integer totalAmount,
        String tossOrderId,
        String tossPaymentMethod,
        String tossPaymentStatus,
        LocalDateTime requestedAt,
        LocalDateTime approvedAt,
        Integer purchasedBlossomCount,
        String paymentKey
) {}
