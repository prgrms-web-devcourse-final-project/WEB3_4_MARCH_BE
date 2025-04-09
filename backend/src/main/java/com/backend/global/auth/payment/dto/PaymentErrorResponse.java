package com.backend.global.auth.payment.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentErrorResponse {
    private int code;
    private String message;
}
