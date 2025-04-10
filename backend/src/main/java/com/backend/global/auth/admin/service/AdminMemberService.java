package com.backend.global.auth.admin.service;


import com.backend.domain.member.entity.Member;
import com.backend.domain.member.entity.Role;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.global.auth.admin.dto.AdminMemberDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AdminMemberService {

    private final MemberRepository memberRepository;
    private final AdminLogService adminLogService;

    public AdminMemberService(MemberRepository memberRepository, AdminLogService adminLogService) {
        this.memberRepository = memberRepository;
        this.adminLogService = adminLogService;
    }

    // 회원 목록 조회 (검색 시 이름 또는 이메일 검색)
    public Page<AdminMemberDto> findMembers(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.isBlank()) {
            return memberRepository.findByNicknameContainingOrEmailContaining(keyword, keyword, pageable)
                    .map(AdminMemberDto::fromEntity);
        }
        return memberRepository.findAll(pageable).map(AdminMemberDto::fromEntity);
    }

    public AdminMemberDto getMemberDetail(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
        return AdminMemberDto.fromEntity(member);
    }

    // 회원 차단/정지 처리: MemberStatus를 BLOCKED로 업데이트
    public void blockMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
        member.block();
        memberRepository.save(member);
        adminLogService.logAction("BLOCK_MEMBER", "회원 " + memberId + " 차단/정지 처리");
    }

    // 회원 탈퇴 처리: soft delete (isDeleted 플래그 업데이트 및 상태 변경)
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
        member.withdraw();
        memberRepository.save(member);
        adminLogService.logAction("DELETE_MEMBER", "회원 " + memberId + " 탈퇴 처리");
    }

    // 회원 역할 변경
    public void updateMemberRole(Long memberId, String newRole) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
        member.updateRole(Role.valueOf(newRole));
        memberRepository.save(member);
        adminLogService.logAction("UPDATE_MEMBER_ROLE", "회원 " + memberId + " 역할을 " + newRole + "로 변경");
    }
}