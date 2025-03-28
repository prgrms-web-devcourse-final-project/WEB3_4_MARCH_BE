package com.backend.domain.image.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 사용자 (회원)
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "user_id", nullable = false)
    // private Member user;

    // 이미지 key
    @Column(nullable = false)
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
