package com.backend.global.auth.admin.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


/**
 * 관리자 동작 로그 엔티티
 * 관리자가 수행한 작업을 기록하는 클래스
 */

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String actionType;      // 작업 유형
    private String description;     // 작업 설명
    private LocalDateTime timestamp;    // 로그 발생시간
}
