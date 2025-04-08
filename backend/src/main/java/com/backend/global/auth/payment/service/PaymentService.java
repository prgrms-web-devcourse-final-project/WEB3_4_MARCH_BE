package com.backend.global.auth.payment.service;

import com.backend.domain.member.entity.Member;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.global.auth.payment.dto.PaymentApprovalRequestDto;
import com.backend.global.auth.payment.dto.PaymentApprovalResponseDto;
import com.backend.global.auth.payment.entity.BlossomTransaction;
import com.backend.global.auth.payment.repository.BlossomTransactionRepository;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentService {

    private final MemberRepository memberRepository;
    private final BlossomTransactionRepository transactionRepository;

    public PaymentService(MemberRepository memberRepository, BlossomTransactionRepository transactionRepository) {
        this.memberRepository = memberRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public PaymentApprovalResponseDto approvePayment(Long memberId, PaymentApprovalRequestDto requestDto) {
        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.MEMBER_NOT_FOUND));

        // (실제 환경에서는 TossPayments API 호출 후 받은 결과를 사용)
        // 테스트 환경에서는 아래와 같이 모의 응답값을 사용합니다.
        String tossPaymentMethod = "CARD";
        String tossPaymentStatus = "APPROVED";
        LocalDateTime approvedAt = LocalDateTime.now();

        // 결제 금액에 따른 구매한 블로썸 수량 결정 (예시)
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

        // 회원 잔액 업데이트 – Member 엔티티에 addBlossomBalance() 메서드를 추가했다고 가정
        member.addBlossomBalance(purchasedBlossomCount);
        Integer updatedBalance = member.getBlossomBalance();

        // 블로썸 거래 내역 기록
        BlossomTransaction transaction = BlossomTransaction.builder()
                .member(member)
                .totalAmount(requestDto.amount())
                .tossOrderId(requestDto.orderId())
                .tossPaymentMethod(tossPaymentMethod)
                .tossPaymentStatus(tossPaymentStatus)
                .requestedAt(LocalDateTime.now())  // 실제 요청 시간으로 대체 가능
                .approvedAt(approvedAt)
                .purchasedBlossomCount(purchasedBlossomCount)
                .paymentKey(requestDto.paymentKey())
                .build();
        transactionRepository.save(transaction);

        // PaymentApprovalResponseDto 생성 및 반환
        return new PaymentApprovalResponseDto(
                requestDto.orderId(),            // tossOrderId
                requestDto.paymentKey(),
                requestDto.amount(),
                tossPaymentMethod,
                tossPaymentStatus,
                approvedAt,
                purchasedBlossomCount,
                updatedBalance
        );
    }
}
