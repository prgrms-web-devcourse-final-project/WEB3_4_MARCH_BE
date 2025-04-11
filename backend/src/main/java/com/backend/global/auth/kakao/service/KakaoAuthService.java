package com.backend.global.auth.kakao.service;

import com.backend.domain.member.entity.Member;
import com.backend.domain.member.entity.Role;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.domain.member.service.MemberService;
import com.backend.global.auth.kakao.dto.KakaoTokenResponseDto;
import com.backend.global.auth.kakao.dto.KakaoUserInfoResponseDto;
import com.backend.global.auth.kakao.dto.LoginResponseDto;
import com.backend.global.auth.kakao.util.JwtUtil;
import com.backend.global.auth.kakao.util.KakaoAuthUtil;
import com.backend.global.auth.kakao.util.TokenProvider;
import com.backend.global.config.AdminWhitelistProperties;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

/**
 * 카카오 인가 코드를 받아 회원 정보를 조회하고 JWT 토큰을 발급하는 인증 서비스
 * 회원이 존재하지 않으면 자동으로 회원가입 진행
 * 발급한 리프레시 토큰은 Redis와 쿠키에 저장
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoAuthService {
    // 카카오 API 호출 관련 유틸
    private final KakaoAuthUtil kakaoAuthUtil;
    private final WebClient webClient;

    // JWT 관련 유틸
    private final TokenProvider tokenProvider;
    private final JwtUtil jwtUtil;

    // 회원 등록/조회 서비스
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    // Redis에 리프레시 토큰 저장
    private final RedisRefreshTokenService redisRefreshTokenService;

    // 쿠키 관련 서비스 및 유틸
    private final CookieService cookieService;

    // 관리자 계정 카카오 Id
//    @Value("${admin.whitelist.kakaoIds:}")
//    private Long[] adminKakaoIds;

    // 관리자 계정 이메일
//    @Value("${admin.whitelist.emailDomains:}")
//    private String[] adminEmailDomains;

    private final AdminWhitelistProperties adminWhitelistProperties;
    private final


    /**
     * 카카오 로그인 인가 요청 URL 반환
     */
    public String getKakaoAuthorizationUrl() {
        return kakaoAuthUtil.getKakaoAuthorizationUrl();
    }

    /**
     * 인가 코드를 통해 카카오로부터 토큰 발급받기
     */

//    public KakaoTokenResponseDto getTokenFromKakao(String code) {
//        return webClient.post()
//                .uri(kakaoAuthUtil.getKakaoLoginTokenUrl(code))
//                .retrieve()
//                .bodyToMono(KakaoTokenResponseDto.class)
//                .block();
//    }

    public KakaoTokenResponseDto getTokenFromKakao(String code) {
        // 인가 코드 로그 출력
        log.info("[카카오 로그인] 전달받은 인가 코드: {}", code);

        // KakaoAuthUtil에서 토큰 발급 엔드포인트 URL만 반환하도록 수정
        String tokenUrl = kakaoAuthUtil.getKakaoTokenUrl(); // TOKEN_URL만 반환

        KakaoTokenResponseDto kakaoTokenDto = webClient.post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", kakaoAuthUtil.getGrantType())
                        .with("client_id", kakaoAuthUtil.getClientId())
                        .with("redirect_uri", kakaoAuthUtil.getRedirectUri())
                        .with("code", code))
                .retrieve()
                .bodyToMono(KakaoTokenResponseDto.class)
                .block();

        // 토큰 응답 null 체크
        if (kakaoTokenDto == null) {
            log.error("❌ [카카오 로그인] KakaoTokenResponseDto가 null입니다. 토큰 발급 실패");
            throw new GlobalException(GlobalErrorCode.KAKAO_LOGIN_FAILED, "카카오 토큰 발급 실패");
        }

        return kakaoTokenDto;
    }


    /**
     * 카카오 accessToken을 사용해 사용자 정보 조회
     */
    public KakaoUserInfoResponseDto getUserInfo(String accessToken) {
        return webClient.get()
                .uri(kakaoAuthUtil.getUserInfoUrl())
                .headers(h -> {
                    h.setBearerAuth(accessToken);
                    h.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                })
                .retrieve()
                .bodyToMono(KakaoUserInfoResponseDto.class)
                .block();
    }

    /**
     * 카카오 인가 코드를 통해 로그인 또는 회원가입 처리 후 JWT 토큰 발급
     */

    // 회원 조회 및 회원가입
    // processLogin 메서드에 HttpServletRequest 추가하여 클라이언트 정보(IP, User-Agent)를 전달
    @Transactional
    public LoginResponseDto processLogin(String code, HttpServletRequest request, HttpServletResponse response) {
        // 1. 인가 코드로 accessToken, refreshToken 발급받기
        KakaoTokenResponseDto kakaoTokenDto = getTokenFromKakao(code);
        String kakaoAccessToken = kakaoTokenDto.accessToken();
        String kakaoRefreshToken = kakaoTokenDto.refreshToken();

        // 2. 카카오 accessToken으로 사용자 정보 요청
        KakaoUserInfoResponseDto kakaoUserInfo = getUserInfo(kakaoAccessToken);
        Long kakaoId = kakaoUserInfo.id();

        // 관리자 화이트리스트에서 카카오 Id, 이메일 가져오기
        List<Long> kakaoIds = adminWhitelistProperties.getKakaoIds();
        List<String> emailDomains = adminWhitelistProperties.getEmailDomains();

        // 3. 해당 kakaoId가 등록된 사용자인지 확인 (회원인지 아닌지 확인)
        Optional<Member> optionalMember = memberRepository.findByKakaoId(kakaoId);
        boolean isRegistered = optionalMember.isPresent();
        Member member;

        log.info("🧪 [admin list] kakaoIds={}", adminWhitelistProperties.getKakaoIds());
        log.info("🧪 [admin list] emailDomains={}", adminWhitelistProperties.getEmailDomains());
        log.info("🧪 [current user] kakaoId={}", kakaoId);

        if (isRegistered) {
            // 3-1. 이미 등록된 회원인 경우, 기존 회원 정보 조회
            member = optionalMember.get();
            // **관리자 화이트리스트 조건을 재검사하여, 필요한 경우 관리자 역할로 업데이트**
            boolean isAdmin = false;

            // 카카오ID 화이트리스트 체크
            if (kakaoIds != null && kakaoIds.contains(kakaoId)) {
                isAdmin = true;
                log.info("[KakaoAuthService] 관리자 ID 일치: kakaoId={} matched with whitelist", kakaoId);
            }

            // 이메일 화이트리스트 체크
            String email = kakaoUserInfo.kakaoAccount().email();
            if (email != null && emailDomains != null && emailDomains.contains(email.trim().toLowerCase())) {
                isAdmin = true;
                log.info("[KakaoAuthService] 관리자 이메일 일치: email={} matched with whitelist", email);
            }

            // 관리자 권한으로 변경
            if (isAdmin && !member.getRole().equals(Role.ROLE_ADMIN)) {
                member.updateRole(Role.ROLE_ADMIN); // Role 업데이트
                memberRepository.saveAndFlush(member);
                member = memberRepository.findById(member.getId()).orElseThrow();
                log.info("[KakaoAuthService] 관리자 권한 반영 완료: memberId={}, Role={}", member.getId(), member.getRole());
            }

            if (member.getRole().equals(Role.ROLE_ADMIN)) {
                log.info("[KakaoAuthService] 관리자 계정 로그인 성공: memberId={}", member.getId());
            }

        } else {
            // 3-2. 신규 회원 → 아직 DB에는 등록하지 않음 (회원가입 전 단계)
            // 이후 /members/register 에서 최종 등록 예정
            // 신규 유저면 DB에 등록하지 않고 member 객체만 생성
            member = Member.ofKakaoUser(
                    kakaoId,
                    kakaoUserInfo.kakaoAccount().email(),
                    kakaoUserInfo.properties().nickname(),
                    Role.ROLE_TEMP_USER
            );

            // 관리자 계정 조건: 카카오ID 또는 이메일이 관리자 목록에 있으면 관리자 권한 ROLE_ADMIN 부여
            boolean isAdmin = false;
            // 카카오ID 화이트리스트 체크
            if (kakaoIds != null && kakaoIds.contains(kakaoId)) {
                isAdmin = true;
                log.info("[KakaoAuthService] 관리자 ID 일치: kakaoId={} matched with whitelist", kakaoId);
            }


            // 이메일 화이트리스트 체크
            String email = kakaoUserInfo.kakaoAccount().email();
            if (email != null && emailDomains != null && emailDomains.contains(email.trim().toLowerCase())) {
                isAdmin = true;
                log.info("[KakaoAuthService] 관리자 이메일 일치: email={} matched with whitelist", email);
            }
            // 관리자 계정으로 확인되면
            if (isAdmin) {
                member.updateRole(Role.ROLE_ADMIN); // 권한을 관리자로 변경
            }

            memberRepository.save(member); // id 부여 목적
        }


        // 4. JWT access, refresh 토큰 생성
        // JWT 토큰 발급 시 권한은 member.getRole() 기준으로 생성
        String accessToken = tokenProvider.createAccessToken(member.getId(), member.getRole().name());
        String refreshToken = tokenProvider.createRefreshToken(member.getId());
        long ttl = tokenProvider.getRefreshTokenExpiration();


        // 5. refreshToken 내 jti 추출
        String jti = tokenProvider.parseToken(refreshToken).getId();
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        // 6. Redis에 리프레시 토큰 저장 (중복 로그인 방지)
        // Redis에 jti를 포함해 refreshToken과 클라이언트 정보 저장
        redisRefreshTokenService.saveRefreshToken(member.getId(), jti, refreshToken, ttl, ip, userAgent);

        // 7. 리프레시 토큰을 쿠키에 저장
        cookieService.addRefreshTokenToCookie(refreshToken, response);

        // 8. 응답 DTO 반환
        return LoginResponseDto.of(accessToken, kakaoId, isRegistered ? member.getId() : null, refreshToken, isRegistered);
    }


    /**
     * 카카오 리프레시 토큰을 통해 accessToken 재발급
     */
    public LoginResponseDto reissueTokens(String refreshToken, HttpServletRequest request) {
        // JWT에서 memberId 추출
        Long memberId = tokenProvider.extractMemberId(refreshToken);

        // 회원 조회 (DB에서 기본 정보만)
        Member member = memberService.getMemberEntity(memberId);

        // 새로운 JWT 토큰 발급
        String newAccessToken = tokenProvider.createAccessToken(member.getId(), member.getRole().name());
        String newRefreshToken = tokenProvider.createRefreshToken(member.getId());
        long ttl = tokenProvider.getRefreshTokenExpiration();

        // 새 토큰의 jti 추출 및 클라이언트 정보 재확인
        String newJti = tokenProvider.parseToken(newRefreshToken).getId();
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        // Redis 저장 (기존 토큰 갱신)
        redisRefreshTokenService.saveRefreshToken(member.getId(), newJti, newRefreshToken, ttl, ip, userAgent);

        return LoginResponseDto.of(newAccessToken, member.getKakaoId(), member.getId(), newRefreshToken, true);
    }
}
