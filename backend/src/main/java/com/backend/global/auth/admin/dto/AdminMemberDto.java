package com.backend.global.auth.admin.dto;

import com.backend.domain.member.entity.Member;
import com.backend.domain.member.entity.MemberStatus;
import com.backend.domain.member.entity.Role;

public record AdminMemberDto(
        Long id,
        String nickname,
        String email,
        Role role,
        MemberStatus status,
        boolean isDeleted
) {
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
