package com.backend.global.auth.payment.dto;

public record CancelPaymentRequest(String paymentKey, String cancelReason) {
}

