package com.backend.global.auth.payment.controller;

import com.backend.global.auth.payment.dto.BlossomTransactionDto;
import com.backend.global.auth.payment.entity.BlossomTransaction;
import com.backend.global.auth.payment.repository.BlossomTransactionRepository;
import com.backend.global.response.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment/history")
public class PaymentHistoryController {

    private final BlossomTransactionRepository transactionRepository;

    @GetMapping("/{memberId}")
    public ResponseEntity<GenericResponse<List<BlossomTransactionDto>>> getTransactionHistory(@PathVariable Long memberId) {
        List<BlossomTransaction> blossomTransactions = transactionRepository.findByMemberId(memberId);
        List<BlossomTransactionDto> blossomTransactionDto = blossomTransactions.stream()
                .map(tx -> new BlossomTransactionDto(
                        tx.getId(),
                        tx.getMember().getId(),
                        tx.getTotalAmount(),
                        tx.getTossOrderId(),
                        tx.getTossPaymentMethod(),
                        tx.getTossPaymentStatus(),
                        tx.getRequestedAt(),
                        tx.getApprovedAt(),
                        tx.getPurchasedBlossomCount(),
                        tx.getPaymentKey()
                ))
                .collect(Collectors.toList());
        GenericResponse<List<BlossomTransactionDto>> response = GenericResponse.ok(blossomTransactionDto, "거래 내역 조회에 성공하였습니다.");
        return ResponseEntity.ok(response);
    }
}
