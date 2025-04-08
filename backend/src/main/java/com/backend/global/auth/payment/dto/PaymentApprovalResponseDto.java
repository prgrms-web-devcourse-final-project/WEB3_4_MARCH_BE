package com.backend.global.auth.payment.dto;

import java.time.LocalDateTime;

public record PaymentApprovalResponseDto(
        String tossOrderId,
        String paymentKey,
        Integer totalAmount,
        String tossPaymentMethod,
        String tossPaymentStatus,
        LocalDateTime approvedAt,
        Integer purchasedBlossomCount,
        Integer updatedBlossomBalance
) {}