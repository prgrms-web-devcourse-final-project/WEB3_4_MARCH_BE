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
        return ResponseEntity.ok("Payment temp save successful");
    }

    // 2️⃣ orderId, amount 검증 API
    @PostMapping("/verifyAmount")
    public ResponseEntity<?> verifyAmount(HttpSession session, @RequestBody SaveAmountRequest saveAmountRequest) {
        String savedAmount = (String) session.getAttribute(saveAmountRequest.orderId());
        if (savedAmount == null || !savedAmount.equals(saveAmountRequest.amount())) {
            return ResponseEntity.badRequest()
                    .body(PaymentErrorResponse.builder().code(400).message("결제 금액 정보가 유효하지 않습니다.").build());
        }
        session.removeAttribute(saveAmountRequest.orderId());
        return ResponseEntity.ok("Payment is valid");
    }

    // 3️⃣ 결제 승인 요청 API (기존)
    @PostMapping("/confirm")
    public GenericResponse<PaymentApprovalResponseDto> confirmPayment(@RequestParam Long memberId,
                                                                      @RequestBody PaymentApprovalRequestDto requestDto) {
        PaymentApprovalResponseDto responseDto = paymentService.approvePayment(memberId, requestDto);
        return GenericResponse.ok(responseDto, "결제 승인에 성공하였습니다.");
    }

    // 4️⃣ 결제 조회 API
    @GetMapping("/{id}")
    public ResponseEntity<?> getPayment(@PathVariable("id") String backendOrderId) {
        PaymentApprovalResponseDto responseDto = paymentService.getPayment(backendOrderId);
        return ResponseEntity.ok(responseDto);
    }

    // 5️⃣ 결제 취소 API
    @PostMapping("/cancel")
    public ResponseEntity<?> cancelPayment(@RequestBody CancelPaymentRequest cancelPaymentRequest) throws IOException, InterruptedException {
        HttpResponse<String> response = tossPaymentClient.requestPaymentCancel(cancelPaymentRequest.paymentKey(), cancelPaymentRequest.cancelReason());
        if(response.statusCode() == 200) {
            paymentService.changePaymentStatus(cancelPaymentRequest.paymentKey(), "CANCELED");
        }
        return ResponseEntity.status(response.statusCode()).body(response.body());
    }
}