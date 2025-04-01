package com.backend.domain.member.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.backend.domain.image.service.ImageService;
import com.backend.domain.image.service.PresignedService;
import com.backend.domain.member.dto.MemberInfoDto;
import com.backend.domain.member.dto.MemberModifyRequestDto;
import com.backend.domain.member.dto.MemberRegisterRequestDto;
import com.backend.domain.member.dto.MemberResponseDto;
import com.backend.domain.member.service.MemberService;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import com.backend.global.response.GenericResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;
    private final PresignedService presignedService;
    private final ImageService imageService;

    // 회원 가입 (카카오 로그인 이후)
    @PostMapping("/register")
    public ResponseEntity<GenericResponse<MemberInfoDto>> registerMember(
        @RequestPart("member") MemberRegisterRequestDto requestDto,
        @RequestPart(value = "files", required = false) MultipartFile[] files) throws IOException {

        if (files == null || files.length < 1 || files.length > 5) {
            throw new GlobalException(GlobalErrorCode.IMAGE_COUNT_INVALID);
        }

        // 1. 회원 기본 정보로 회원 생성 (이미지 정보는 없음)
        MemberInfoDto memberInfo = memberService.registerMember(requestDto);

        // 2. 이미지 파일들을 PresignedService.uploadFiles()를 통해 S3 업로드 및 DB 등록
        //    여기서는 List<MultipartFile>가 필요하므로 배열을 List로 변환합니다.
        presignedService.uploadFiles(java.util.Arrays.asList(files), memberInfo.id());

        // 3. 최신 회원 정보를 다시 조회하여 반환 (profileImage 등 업데이트 반영)
        MemberInfoDto updatedInfo = memberService.getMemberInfo(memberInfo.id());
        return ResponseEntity.ok(GenericResponse.of(updatedInfo));
    }

    // 회원 정보 조회
    // 클라이언트(프론트엔드)에 회원 정보 응답할 때 사용
    @GetMapping("/{memberId}")
    public ResponseEntity<GenericResponse<MemberInfoDto>> getMemberInfo(@PathVariable Long memberId) {
        MemberInfoDto responseDto = memberService.getMemberInfo(memberId);
        return ResponseEntity.ok().body(GenericResponse.of(responseDto));
    }

    // 닉네임 검색
    @GetMapping("/search")
    public ResponseEntity<GenericResponse<List<MemberInfoDto>>> searchMembersByNickname(@RequestParam String nickname) {
        List<MemberInfoDto> members = memberService.searchByNickname(nickname);
        return ResponseEntity.ok().body(GenericResponse.of(members));
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
