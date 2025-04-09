package com.backend.global.auth.payment.dto;

public record SaveAmountRequest(String orderId, String amount) {
}
