package com.backend.domain.image.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "images")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 유저 Id
    @Column(nullable = false)
    private Long userId;

    // 이미지 URL
    @Column(nullable = false)
    private String url;

    // 대표 이미지 여부
    @Column(nullable = false)
    private Boolean isPrimary;

    public void updateIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }
}
