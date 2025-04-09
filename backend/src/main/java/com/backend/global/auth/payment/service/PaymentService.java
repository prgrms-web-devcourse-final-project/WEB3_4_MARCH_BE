package com.backend.global.auth.payment.service;

import com.backend.domain.member.entity.Member;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.global.auth.payment.client.TossPaymentClient;
import com.backend.global.auth.payment.dto.PaymentApprovalRequestDto;
import com.backend.global.auth.payment.dto.PaymentApprovalResponseDto;
import com.backend.global.auth.payment.entity.BlossomTransaction;
import com.backend.global.auth.payment.repository.BlossomTransactionRepository;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

@Service
public class PaymentService {

    private final MemberRepository memberRepository;
    private final BlossomTransactionRepository transactionRepository;
    private final TossPaymentClient tossPaymentClient;

    public PaymentService(MemberRepository memberRepository,
                          BlossomTransactionRepository transactionRepository,
                          TossPaymentClient tossPaymentClient) {
        this.memberRepository = memberRepository;
        this.transactionRepository = transactionRepository;
        this.tossPaymentClient = tossPaymentClient;
    }

    /**
     * 결제 승인 요청 API
     * 실제 TossPayments API를 호출하여 결제를 승인 받고,
     * 승인 성공 시 회원 잔액 업데이트 및 거래 내역 기록을 처리합니다.
     */
    @Transactional
    public PaymentApprovalResponseDto approvePayment(Long memberId, PaymentApprovalRequestDto requestDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.MEMBER_NOT_FOUND));

        HttpResponse<String> confirmResponse;
        try {
            confirmResponse = tossPaymentClient.requestConfirm(
                    requestDto.paymentKey(),
                    requestDto.orderId(),
                    requestDto.amount()
            );
        } catch (IOException | InterruptedException e) {
            throw new GlobalException(GlobalErrorCode.SERVER_ERROR, "결제 승인 요청 중 오류 발생: " + e.getMessage());
        }
        if (confirmResponse.statusCode() != 200) {
            throw new GlobalException(GlobalErrorCode.INVALID_REQUEST, "결제 승인 실패: " + confirmResponse.body());
        }

        // 실제 API 응답의 상세 데이터 파싱은 필요에 따라 구현(여기서는 단순화)
        String tossPaymentMethod = "CARD";
        String tossPaymentStatus = "APPROVED";
        LocalDateTime approvedAt = LocalDateTime.now();

        Integer purchasedBlossomCount;
        switch (requestDto.amount()) {
            case 5000:
                purchasedBlossomCount = 50;
                break;
            case 9500:
                purchasedBlossomCount = 100;
                break;
            case 26500:
                purchasedBlossomCount = 300;
                break;
            default:
                throw new GlobalException(GlobalErrorCode.INVALID_REQUEST);
        }

        // 회원 잔액 업데이트
        member.addBlossomBalance(purchasedBlossomCount);
        Integer updatedBalance = member.getBlossomBalance();

        BlossomTransaction transaction = BlossomTransaction.builder()
                .member(member)
                .totalAmount(requestDto.amount())
                .tossOrderId(requestDto.orderId())
                .tossPaymentMethod(tossPaymentMethod)
                .tossPaymentStatus(tossPaymentStatus)
                .requestedAt(LocalDateTime.now())
                .approvedAt(approvedAt)
                .purchasedBlossomCount(purchasedBlossomCount)
                .paymentKey(requestDto.paymentKey())
                .build();
        try {
            transactionRepository.save(transaction);
        } catch (Exception ex) {
            try {
                HttpResponse<String> cancelResponse = tossPaymentClient.requestPaymentCancel(requestDto.paymentKey(), "DB 오류로 인한 취소");
                // 취소 응답 로깅 또는 추가 보상 처리 가능
            } catch (IOException | InterruptedException cancelEx) {
                // 결제 취소 요청 실패 시 로깅 등 처리
            }
            throw new GlobalException(GlobalErrorCode.SERVER_ERROR, "결제 승인 처리 중 오류 발생 및 결제 취소됨.");
        }

        return new PaymentApprovalResponseDto(
                requestDto.orderId(),
                requestDto.paymentKey(),
                requestDto.amount(),
                tossPaymentMethod,
                tossPaymentStatus,
                approvedAt,
                purchasedBlossomCount,
                updatedBalance
        );
    }

    /**
     * 결제 조회 API를 위한 메서드
     * tossOrderId (backendOrderId)로 결제 정보를 조회합니다.
     */
    @Transactional(readOnly = true)
    public PaymentApprovalResponseDto getPayment(String backendOrderId) {
        BlossomTransaction transaction = transactionRepository.findByTossOrderId(backendOrderId)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.INVALID_REQUEST, "결제 정보를 찾을 수 없습니다."));
        return new PaymentApprovalResponseDto(
                transaction.getTossOrderId(),
                transaction.getPaymentKey(),
                transaction.getTotalAmount(),
                transaction.getTossPaymentMethod(),
                transaction.getTossPaymentStatus(),
                transaction.getApprovedAt(),
                transaction.getPurchasedBlossomCount(),
                transaction.getMember().getBlossomBalance()
        );
    }

    /**
     * 결제 취소 후 상태 변경 처리
     */
    @Transactional
    public void changePaymentStatus(String paymentKey, String newStatus) {
        BlossomTransaction transaction = transactionRepository.findByPaymentKey(paymentKey)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.INVALID_REQUEST, "결제 정보를 찾을 수 없습니다."));
        transaction.setTossPaymentStatus(newStatus);
    }
}