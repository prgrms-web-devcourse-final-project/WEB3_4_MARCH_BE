package com.backend.domain.member.controller;

import com.backend.domain.image.service.ImageService;
import com.backend.domain.member.dto.*;
import com.backend.domain.member.service.MemberService;
import com.backend.domain.userkeyword.dto.request.UserKeywordSaveRequest;
import com.backend.domain.userkeyword.service.UserKeywordService;
import com.backend.global.auth.kakao.service.CookieService;
import com.backend.global.auth.kakao.util.TokenProvider;
import com.backend.global.auth.model.CustomUserDetails;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import com.backend.global.response.GenericResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final ImageService imageService;
    private final TokenProvider tokenProvider;
    private final CookieService cookieService;
    private final UserKeywordService userKeywordService;


    /**
     * 회원 가입에 필요한 모든 데이터를 JSON으로 받도록 수정됨 (수정됨)
     * RegisterDTO: 기존에 사용하려 했던 Base64ImagesWrapper 대신,
     * 여기서는 files 필드에 base64 문자열 배열을 직접 받습니다.
     */
    record RegisterDTO (
        MemberRegisterRequestDto member,
        String[] files,
        UserKeywordSaveRequest keywords
    ) {}
    /**
     * 회원 가입을 처리하는 엔드포인트이다.
     *
     * <p>
     * 클라이언트로부터 회원 가입 정보와 1장 이상 5장 이하의 이미지 파일을 전달받아,
     * 1. 회원 기본 정보를 등록한 후,
     * 2. 이미지 파일들을 S3에 업로드하고, 해당 이미지들을 DB에 등록한다.
     * 이때, 첫 번째 업로드된 이미지는 자동으로 대표 이미지로 지정된다.
     * 3. 최종적으로 업로드된 이미지를 반영한 최신 회원 정보를 반환한다.
     * </p>
     *
     * 입력 예시
     * {
     *   "member": { ... 회원 기본 정보 ... },
     *   "files": [ "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEASABIAAD...", ... ],
     *   "keywords": { "keywordIds": [1, 2, 3] }
     * }
     *
     * @param data   회원 생성 시 필요한 정보를 담은 RegisterDTO
     * @return 회원 가입에 성공한 회원의 최신 정보를 담은 응답 객체 (MemberInfoDto)
     * @throws IOException     파일 처리 중 I/O 예외가 발생할 경우
     * @throws GlobalException 이미지 파일 수가 1장 미만이거나 5장을 초과할 경우 IMAGE_COUNT_INVALID 오류 발생
     */
	@PostMapping("/register")
    public ResponseEntity<GenericResponse<MemberRegisterResponseDto>> register(
            @RequestBody RegisterDTO data,
            HttpServletResponse response
    ) throws IOException {
            MemberRegisterRequestDto memberReq = data.member;
            String[] base64Images = data.files;
            UserKeywordSaveRequest keywordReq = data.keywords;

            if (base64Images == null || base64Images.length < 1 || base64Images.length > 5) {
                throw new GlobalException(GlobalErrorCode.IMAGE_COUNT_INVALID);
            }

            // 1. 회원 기본 정보로 회원 생성 (이미지 정보는 없음)
            MemberInfoDto memberInfo = memberService.registerMember(memberReq);
            log.info("통과 1");

            // 2. 이미지 파일들을 PresignedService.uploadFiles()를 통해 S3 업로드 및 DB 등록
            imageService.uploadBase64Images(base64Images, memberInfo.id());
            log.info("통과 2");

            // 3. 선택한 키워드 저장
            userKeywordService.saveUserKeywords(memberInfo.id(), keywordReq.getKeywordIds());
            memberService.setRole(memberInfo.id());
            log.info("통과 3");

            // 4. 최신 회원 정보를 다시 조회하여 반환 (profileImage 등 업데이트 반영)
            MemberInfoDto updatedInfo = memberService.getMemberInfoForInternal(memberInfo.id());
            log.info("통과 4");
            // 5. 토큰 재발급 (ROLE_TEMP_USER -> ROLE_USER 로 role 변경시 토큰 재발급이 필요)
            String accessToken = tokenProvider.createAccessToken(updatedInfo.id(), updatedInfo.role().name());
            String refreshToken = tokenProvider.createRefreshToken(updatedInfo.id());
            log.info("통과 5");

            // 6. 새로 발급된 토큰을 쿠키에 저장.
            cookieService.addAccessTokenToCookie(accessToken, response);
            cookieService.addRefreshTokenToCookie(refreshToken, response);
            log.info("통과 6");

            // 7. 응답 DTO 생성
            MemberRegisterResponseDto responseDto = new MemberRegisterResponseDto(updatedInfo, accessToken, refreshToken);

            return ResponseEntity.ok(GenericResponse.of(responseDto, "회원 등록이 완료되었습니다."));
    }

    // 다른 멤버 정보 조회
    // 클라이언트(프론트엔드)에 회원 정보 응답할 때 사용
    @GetMapping("/{memberId}")
    public ResponseEntity<GenericResponse<MemberResponseDto>> getMemberProfile(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                               @PathVariable Long memberId) {
        MemberResponseDto responseDto = memberService.getMemberInfo(customUserDetails, memberId);
        return ResponseEntity.ok().body(GenericResponse.of(responseDto, "회원 프로필 조회가 완료되었습니다."));
    }

    // 내 정보 조회
    @GetMapping("/me")
    public ResponseEntity<GenericResponse<MemberResponseDto>> getMyProfile(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        MemberResponseDto responseDto = memberService.getMemberInfo(customUserDetails, customUserDetails.getMemberId());

        return ResponseEntity.ok().body(GenericResponse.of(responseDto, "자신의 프로필 조회가 완료되었습니다."));
    }

    // 닉네임으로 회원 검색
    @GetMapping("/search")
    public ResponseEntity<GenericResponse<List<MemberResponseDto>>> searchMembersByNickname(@RequestParam String nickname) {
        List<MemberResponseDto> members = memberService.searchByNickname(nickname);
        return ResponseEntity.ok().body(GenericResponse.of(members, "회원 검색이 완료되었습니다."));
    }

    // 회원 정보 수정
    record ModifyMemberDto(
        MemberModifyRequestDto member,
        String[] newImages,
        List<Long> keepImageIds,
        UserKeywordSaveRequest keywords
    ) {}
    @PatchMapping("/{id}")
    public ResponseEntity<GenericResponse<MemberResponseDto>> modifyMemberInfo(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("id") Long memberId,
            @RequestBody ModifyMemberDto data
    ) throws IOException {

            MemberModifyRequestDto memberReq = data.member;
            String[] base64NewImages = data.newImages;
            List<Long> keepIds = data.keepImageIds;
            UserKeywordSaveRequest keywordReq = data.keywords;

            MemberResponseDto updated = memberService.modifyMember(
                memberId,
                memberReq,
                keepIds,
                base64NewImages,
                keywordReq
            );
        return ResponseEntity.ok(GenericResponse.of(updated, "회원 정보 수정 완료"));
    }

    // 닉네임 중복 검사
    @GetMapping("/checkNickname")
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
        return ResponseEntity.ok().body(GenericResponse.of(responseDto, "회원 탈퇴가 완료되었습니다."));
    }

    // 사용자 위치 정보 갱신 (사용자가 위치 정보 갱신 버튼 클릭 시 호출)
    @PatchMapping("/{memberId}/location")
    public ResponseEntity<GenericResponse<MemberResponseDto>> updateLocation(
            @PathVariable Long memberId,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {

        MemberResponseDto responseDto = memberService.updateLocation(memberId, latitude, longitude);
        return ResponseEntity.ok().body(GenericResponse.of(responseDto, "위치 정보가 업데이트되었습니다."));
    }
}
