package com.backend.global.auth.payment.entity;

import com.backend.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "blossom_transaction")
public class BlossomTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 결제한 회원과의 연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 결제한 총 금액
    @Column(nullable = false)
    private Integer totalAmount;

    // 프론트에서 지정한 주문 ID (중복되지 않는 값)
    @Column(nullable = false, unique = true)
    private String tossOrderId;

    // 결제 방식 (예: CARD)
    @Column(nullable = false)
    private String tossPaymentMethod;

    // 결제 상태 (예: APPROVED)
    @Column(nullable = false)
    private String tossPaymentStatus;

    // 결제 요청 시각
    @Column(nullable = false)
    private LocalDateTime requestedAt;

    // 결제 승인 시각
    @Column
    private LocalDateTime approvedAt;

    // 결제 시 구매한 블로썸 수량 (예: 50, 100, 300 등)
    @Column(nullable = false)
    private Integer purchasedBlossomCount;

    // 결제 관련 키
    @Column(nullable = false, unique = true)
    private String paymentKey;
}
