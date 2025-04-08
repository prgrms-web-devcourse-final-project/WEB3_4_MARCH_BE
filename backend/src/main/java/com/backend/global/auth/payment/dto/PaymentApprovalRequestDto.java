package com.backend.global.auth.payment.dto;

// 프론트엔드에서 결제 승인 요청시 전달하는 데이터
public record PaymentApprovalRequestDto(
        String paymentKey,
        String orderId,
        Integer amount
) {}
