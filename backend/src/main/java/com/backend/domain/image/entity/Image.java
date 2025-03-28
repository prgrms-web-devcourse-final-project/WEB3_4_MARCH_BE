package com.backend.domain.image.entity;

import com.backend.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 사용자 (회원)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 이미지 key
    @Column(name = "`key`") // 백틱으로 감싸기 (MySQL용)
    private String key;

    // 이미지 URL
    @Column(nullable = false)
    private String url;

    // 대표 이미지 여부
    @Column(nullable = false)
    private Boolean isPrimary;

    // 대표 이미지 설정 메서드
    public void updatePrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
    }
}
