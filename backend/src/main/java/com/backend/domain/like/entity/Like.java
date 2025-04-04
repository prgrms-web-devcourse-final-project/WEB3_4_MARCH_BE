package com.backend.domain.like.entity;

import com.backend.global.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "likes")
public class Like extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 좋아요 누른 회원 (보낸 사람)
    private Long senderId;
    
    // 좋아요 받은 회원 (받은 사람)
    private Long receiverId;
    
    // 좋아요 발생 시각은 BaseEntity에서 관리할 수 있음
}
