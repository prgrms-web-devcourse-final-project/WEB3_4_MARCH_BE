package com.backend.domain.member.controller;

import com.backend.domain.member.dto.MemberInfoDto;
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

    // 회원 가입 (카카오 로그인 이후)
    @PostMapping("/register")
    public ResponseEntity<GenericResponse> registerMember(@Valid @RequestBody MemberRegisterRequestDto requestDto) {

        MemberInfoDto responseDto = memberService.registerMember(requestDto);
        return ResponseEntity.ok().body(GenericResponse.of(responseDto));
    }

    // 회원 정보 조회
    // 클라이언트(프론트엔드)에 회원 정보 응답할 때 사용
    @GetMapping("/{memberId}")
    public ResponseEntity<GenericResponse<MemberResponseDto>> getMemberInfo(
            @PathVariable Long memberId) {

        MemberResponseDto responseDto = memberService.getMemberInfo(memberId);
        return ResponseEntity.ok().body(GenericResponse.of(responseDto));
    }

    // 회원 정보 수정
    @PatchMapping("/{memberId}")
    public ResponseEntity<GenericResponse<MemberResponseDto>> modifyMember(
            @PathVariable Long memberId,
            @Valid @RequestBody MemberModifyRequestDto requestDto) {

        MemberResponseDto responseDto = memberService.modifyMember(memberId, requestDto);
        return ResponseEntity.ok().body(GenericResponse.of(responseDto));
    }

    // 닉네임 중복 검사
    @GetMapping("/check-nickname")
    public ResponseEntity<GenericResponse<Boolean>> checkNickname(@RequestParam String nickname) {
        boolean exists = memberService.isNicknameTaken(nickname);
        if (exists) {
            return ResponseEntity.ok().body(GenericResponse.of(false, "이미 사용 중인 닉네임입니다."));
        }
        return ResponseEntity.ok().body(GenericResponse.of(true, "사용 가능한 닉네임입니다."));
    }

    // 회원 탈퇴
    @DeleteMapping("/{memberId}")
    public ResponseEntity<GenericResponse<MemberResponseDto>> withdrawMember(
            @PathVariable Long memberId) {

        MemberResponseDto responseDto = memberService.withdraw(memberId);
        return ResponseEntity.ok().body(GenericResponse.of(responseDto));
    }

    // 사용자 위치 정보 갱신 (사용자가 위치 정보 갱신 버튼 클릭 시 호출)
    @PatchMapping("/{memberId}/location")
    public ResponseEntity<GenericResponse<MemberResponseDto>> updateLocation(
            @PathVariable Long memberId,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {

        MemberResponseDto responseDto = memberService.updateLocation(memberId, latitude, longitude);
        return ResponseEntity.ok().body(GenericResponse.of(responseDto));
    }
}
