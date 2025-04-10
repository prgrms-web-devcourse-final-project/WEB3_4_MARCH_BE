package com.backend.global.auth.admin.dto;

import com.backend.domain.member.entity.Member;
import com.backend.domain.member.entity.MemberStatus;
import com.backend.domain.member.entity.Role;


/**
 * 관리자 화면에서 회원 정보를 전달하기 위한 DTO
 */

public record AdminMemberDto(
        Long id,
        String nickname,
        String email,
        Role role,
        MemberStatus status,
        boolean isDeleted
) {
    // Member 엔티티를 AdminMemberDto로 변환하는 메서드
    public static AdminMemberDto fromEntity(Member member) {
        return new AdminMemberDto(
                member.getId(),
                member.getNickname(),
                member.getEmail(),
                member.getRole(),
                member.getStatus(),
                member.isDeleted()
        );
    }
}
