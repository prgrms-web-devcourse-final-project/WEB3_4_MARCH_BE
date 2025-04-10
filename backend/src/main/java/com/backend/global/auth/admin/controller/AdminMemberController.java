package com.backend.global.auth.admin.controller;

import com.backend.global.auth.admin.dto.AdminMemberDto;
import com.backend.global.auth.admin.service.AdminMemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/admin/members")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminMemberController {

    private final AdminMemberService adminMemberService;

    public AdminMemberController(AdminMemberService adminMemberService) {
        this.adminMemberService = adminMemberService;
    }

    // 1) 회원 목록 조회 (페이징, 검색)
    @GetMapping
    public ResponseEntity<Page<AdminMemberDto>> getMembers(
            @RequestParam(required = false) String keyword,
            Pageable pageable) {
        Page<AdminMemberDto> memberPage = adminMemberService.findMembers(keyword, pageable);
        return ResponseEntity.ok(memberPage);
    }

    // 2) 회원 상세정보 조회
    @GetMapping("/{memberId}")
    public ResponseEntity<AdminMemberDto> getMemberDetail(@PathVariable Long memberId) {
        AdminMemberDto member = adminMemberService.getMemberDetail(memberId);
        return ResponseEntity.ok(member);
    }

    // 3) 회원 차단/정지 처리
    @PutMapping("/{memberId}/block")
    public ResponseEntity<Void> blockMember(@PathVariable Long memberId) {
        adminMemberService.blockMember(memberId);
        return ResponseEntity.ok().build();
    }

    // 4) 회원 탈퇴 처리 (soft delete)
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long memberId) {
        adminMemberService.deleteMember(memberId);
        return ResponseEntity.ok().build();
    }

    // 5) 회원 역할 변경
    @PutMapping("/{memberId}/role")
    public ResponseEntity<Void> updateMemberRole(
            @PathVariable Long memberId,
            @RequestParam String role) {
        adminMemberService.updateMemberRole(memberId, role);
        return ResponseEntity.ok().build();
    }
}
