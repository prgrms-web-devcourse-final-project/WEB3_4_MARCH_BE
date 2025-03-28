package com.backend.domain.member.controller;

import com.backend.domain.member.dto.MemberModifyRequestDto;
import com.backend.domain.member.dto.MemberRegisterRequestDto;
import com.backend.domain.member.dto.MemberResponseDto;
import com.backend.domain.member.service.MemberService;
import com.backend.global.response.GenericResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    // 회원 정보 조회
    // 클라이언트(프론트엔드)에 회원 정보 응답할 때 사용
    @GetMapping("/{memberId}")
    public ResponseEntity<MemberResponseDto> getMember(@PathVariable Long memberId) {
        MemberResponseDto response = memberService.getMemberInfo(memberId);
        return ResponseEntity.ok(response);
    }

    // 회원 정보 수정
    @PatchMapping("/{memberId}")
    public ResponseEntity<GenericResponse<?>> modifyMember(@PathVariable Long memberId,
                                                           @Valid @RequestBody MemberModifyRequestDto requestDto) {
        memberService.modifyMember(memberId, requestDto);
        return ResponseEntity.ok(GenericResponse.ok("회원 정보가 수정되었습니다."));
    }

    // 회원 가입 (카카오 로그인 이후)
    @PostMapping("/register")
    public ResponseEntity<GenericResponse> registerMember(@Valid @RequestBody MemberRegisterRequestDto requestDto) {
        memberService.registerMember(requestDto);
        return ResponseEntity.ok(GenericResponse.ok("회원 가입이 완료되었습니다."));
    }

    // 닉네임 중복 검사
    @GetMapping("/check-nickname")
    public ResponseEntity<GenericResponse> checkNickname(@RequestParam String nickname) {
        boolean exists = memberService.isNicknameTaken(nickname);
        if (exists) {
            return ResponseEntity.badRequest()
                    .body(GenericResponse.fail("이미 사용 중인 닉네임입니다."));
        }
        return ResponseEntity.ok(GenericResponse.ok("사용 가능한 닉네임입니다."));
    }

    // 회원 탈퇴
    @DeleteMapping("/{memberId}")
    public ResponseEntity<GenericResponse<?>> withdrawMember(@PathVariable Long memberId) {
        memberService.withdraw(memberId);
        return ResponseEntity.ok(GenericResponse.ok("회원 탈퇴가 완료되었습니다."));
    }

    // 위치 정보 갱신
    @PatchMapping("/{memberId}/location")
    public ResponseEntity<GenericResponse<?>> updateLocation(
            @PathVariable Long memberId,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        memberService.updateLocation(memberId, latitude, longitude);
        return ResponseEntity.ok(GenericResponse.ok("위치 정보가 업데이트되었습니다."));
    }
}
