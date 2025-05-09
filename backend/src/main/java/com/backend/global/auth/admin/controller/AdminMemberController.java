package com.backend.global.auth.admin.controller;

import com.backend.domain.member.entity.Member;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.global.auth.admin.dto.AdminMemberDto;
import com.backend.global.auth.admin.service.AdminMemberService;
import com.backend.global.redis.service.RedisGeoService;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * 관리자 전용 컨트롤러 클래스
 * 관리자의 기능이 구현된 클래스
 */

@RestController
@RequestMapping("/api/auth/admin/members")
@PreAuthorize("hasAuthority('ROLE_ADMIN')") // 관리자 권한인 경우에만 접근 가능
public class AdminMemberController {

    private final AdminMemberService adminMemberService;
    private final RedisGeoService redisGeoService;
    private final MemberRepository memberRepository;

    public AdminMemberController(AdminMemberService adminMemberService,
            RedisGeoService redisGeoService, MemberRepository memberRepository) {
        this.adminMemberService = adminMemberService;
        this.redisGeoService = redisGeoService;
        this.memberRepository = memberRepository;
    }

    // 1. 회원 목록 조회 (페이징, 검색)
    @GetMapping
    public ResponseEntity<Page<AdminMemberDto>> getMembers(
            @RequestParam(required = false) String keyword,
            Pageable pageable) {
        Page<AdminMemberDto> memberPage = adminMemberService.findMembers(keyword, pageable);
        return ResponseEntity.ok(memberPage);
    }

    // 2. 회원 상세정보 조회
    @GetMapping("/{memberId}")
    public ResponseEntity<AdminMemberDto> getMemberDetail(@PathVariable Long memberId) {
        AdminMemberDto member = adminMemberService.getMemberDetail(memberId);
        return ResponseEntity.ok(member);
    }

    // 3. 회원 차단/정지 처리
    @PutMapping("/{memberId}/block")
    public ResponseEntity<Void> blockMember(@PathVariable Long memberId) {
        adminMemberService.blockMember(memberId);
        return ResponseEntity.ok().build();
    }

    // 4. 회원 탈퇴 처리 (soft delete)
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long memberId) {
        adminMemberService.deleteMember(memberId);
        return ResponseEntity.ok().build();
    }

    // 5. 회원 역할 변경
    @PutMapping("/{memberId}/role")
    public ResponseEntity<Void> updateMemberRole(
            @PathVariable Long memberId,
            @RequestParam String role) {
        adminMemberService.updateMemberRole(memberId, role);
        return ResponseEntity.ok().build();
    }

    // 6. Redis 데이터 로드하는 API 엔드포인트
    @PostMapping("/redis/reload-geo-data")
    public ResponseEntity<String> reloadGeoData() {
        List<Member> allMembers = memberRepository.findAll();
        int count = 0;

        for (Member member : allMembers) {
            if (member.getLatitude() != null && member.getLongitude() != null) {
                redisGeoService.addLocation(
                        member.getId(),
                        member.getLatitude(),
                        member.getLongitude()
                );
                count++;
            }
        }

        return ResponseEntity.ok("로드 완료: " + count + "개 위치 불러오기 성공");
    }
}
