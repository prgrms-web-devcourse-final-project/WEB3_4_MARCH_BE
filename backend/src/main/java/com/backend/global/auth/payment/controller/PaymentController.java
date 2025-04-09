package com.backend.global.auth.payment.controller;

import com.backend.global.auth.payment.dto.*;
import com.backend.global.auth.payment.service.PaymentService;
import com.backend.global.response.GenericResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.http.HttpResponse;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;

    private final com.backend.global.auth.payment.client.TossPaymentClient tossPaymentClient;

    // 1️⃣ orderId, amount 임시 저장 API
    @PostMapping("/saveAmount")
    public ResponseEntity<?> tempsave(HttpSession session, @RequestBody SaveAmountRequest saveAmountRequest) {
        session.setAttribute(saveAmountRequest.orderId(), saveAmountRequest.amount());
        return ResponseEntity.ok(GenericResponse.ok("결제정보가 성공적으로 임시 저장되었습니다."));
    }

    // 2️⃣ orderId, amount 검증 API
    @PostMapping("/verifyAmount")
    public ResponseEntity<GenericResponse<String>> verifyAmount(HttpSession session,
                                                                @RequestBody SaveAmountRequest saveAmountRequest) {
        String savedAmount = (String) session.getAttribute(saveAmountRequest.orderId());
        if (savedAmount == null || !savedAmount.equals(saveAmountRequest.amount())) {
            return ResponseEntity.badRequest()
                    .body(GenericResponse.fail(400, "결제 금액 정보가 유효하지 않습니다."));
        }
        session.removeAttribute(saveAmountRequest.orderId());
        return ResponseEntity.ok(GenericResponse.ok("결제 정보가 유효합니다."));
    }

    // 3️⃣ 결제 승인 요청 API (기존)
    @PostMapping("/confirm")
    public ResponseEntity<GenericResponse<PaymentApprovalResponseDto>> confirmPayment(@RequestParam Long memberId,
                                                                                      @RequestBody PaymentApprovalRequestDto requestDto) {
        PaymentApprovalResponseDto responseDto = paymentService.approvePayment(memberId, requestDto);
        return ResponseEntity.ok(GenericResponse.ok(responseDto, "결제 승인에 성공하였습니다."));
    }

    // 4️⃣ 결제 조회 API
    @GetMapping("/{id}")
    public ResponseEntity<GenericResponse<PaymentApprovalResponseDto>> getPayment(@PathVariable("id") String backendOrderId) {
        PaymentApprovalResponseDto responseDto = paymentService.getPayment(backendOrderId);
        return ResponseEntity.ok(GenericResponse.ok(responseDto, "결제 조회에 성공하였습니다."));
    }

    // 5️⃣ 결제 취소 API
    @PostMapping("/cancel")
    public ResponseEntity<GenericResponse<String>> cancelPayment(@RequestBody CancelPaymentRequest cancelPaymentRequest)
            throws IOException, InterruptedException {
        HttpResponse<String> response = tossPaymentClient.requestPaymentCancel(
                cancelPaymentRequest.paymentKey(), cancelPaymentRequest.cancelReason());
        if (response.statusCode() == 200) {
            paymentService.changePaymentStatus(cancelPaymentRequest.paymentKey(), "CANCELED");
            return ResponseEntity.status(response.statusCode())
                    .body(GenericResponse.ok(response.body(), "결제 취소에 성공하였습니다."));
        } else {
            return ResponseEntity.status(response.statusCode())
                    .body(GenericResponse.fail(response.statusCode(), response.body()));
        }
    }
}