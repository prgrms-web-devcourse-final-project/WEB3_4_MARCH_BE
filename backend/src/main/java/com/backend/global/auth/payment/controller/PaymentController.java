package com.backend.global.auth.payment.controller;

import com.backend.global.auth.payment.dto.PaymentApprovalRequestDto;
import com.backend.global.auth.payment.dto.PaymentApprovalResponseDto;
import com.backend.global.auth.payment.service.PaymentService;
import com.backend.global.response.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 결제 승인 요청 엔드포인트
     * (실제 서비스에서는 인증된 사용자 정보를 통해 memberId를 추출하는 것이 좋습니다.)
     */
    @PostMapping("/confirm")
    public GenericResponse<PaymentApprovalResponseDto> confirmPayment(@RequestParam Long memberId,
                                                                      @RequestBody PaymentApprovalRequestDto requestDto) {
        PaymentApprovalResponseDto responseDto = paymentService.approvePayment(memberId, requestDto);
        return GenericResponse.ok(responseDto, "결제 승인에 성공하였습니다.");
    }
}
