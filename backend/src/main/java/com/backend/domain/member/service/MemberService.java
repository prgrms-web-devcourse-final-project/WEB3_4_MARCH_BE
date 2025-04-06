package com.backend.domain.member.service;

import com.backend.domain.member.dto.MemberInfoDto;
import com.backend.domain.member.dto.MemberModifyRequestDto;
import com.backend.domain.member.dto.MemberRegisterRequestDto;
import com.backend.domain.member.dto.MemberResponseDto;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.entity.Role;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import com.backend.global.redis.service.RedisGeoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final RedisGeoService redisGeoService;

    // 회원 정보 조회
    // Member 엔티티를 DTO로 변환해서 반환
    @Transactional(readOnly = true)
    public MemberInfoDto getMemberInfo(Long memberId) {
        return memberRepository.findById(memberId)
                .filter(member -> !member.isDeleted())
                .map(MemberInfoDto::from)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.MEMBER_NOT_FOUND));
    }

    // 닉네임으로 조회
    public List<MemberInfoDto> searchByNickname(String nickname) {
        return memberRepository.findByNicknameContaining(nickname).stream()
                .filter(member -> !member.isDeleted())
                .map(MemberInfoDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 회원 가입을 처리한다.
     *
     * <p>
     * 1. 이미 활성화된 회원(탈퇴하지 않은 회원)이 존재하는 경우, 중복 가입 예외를 발생시킨다.
     * 2. 탈퇴된 회원인 경우, 회원 정보를 복구하여 재가입 처리한다.
     * 3. 신규 회원인 경우, 회원 정보를 저장하고 생성된 회원 엔티티를 반환한다.
     * 4. 기본 정보와 이미지 등록 후, 회원은 ROLE_USER로 승격된다.
     * </p>
     *
     * @param requestDto 회원 가입 요청 DTO (회원의 카카오 ID, 이메일, 닉네임, 성별, 나이, 키, 위치 등 정보 포함)
     * @return 가입된 회원의 정보를 담은 MemberInfoDto 객체
     * @throws GlobalException 이미 가입된 회원일 경우 DUPLICATE_MEMBER 오류 발생
     */
    @Transactional
    public MemberInfoDto registerMember(MemberRegisterRequestDto requestDto) {
        // 1. 기존 활성 회원 여부
        Member member = memberRepository.findByKakaoId(requestDto.kakaoId())
            .orElseThrow(() -> new GlobalException(GlobalErrorCode.MEMBER_NOT_FOUND));

        // 2. 탈퇴한 회원이면 복구
        if (member.isDeleted()) {
            member.rejoin(requestDto);
            member.updateRole(Role.ROLE_USER);
            return MemberInfoDto.from(member);
        } else {
            // 임시 회원인지 현재 회원인지 판별 여부
            if (member.getRole() == Role.ROLE_TEMP_USER) {
                member.updateProfile(
                    member.getNickname(),           // 기존 닉네임 유지
                    requestDto.age(),               // 추가 정보: 나이
                    requestDto.height(),            // 추가 정보: 키
                    requestDto.gender(),            // 추가 정보: 성별
                    member.getImages(),             // 기존 이미지 리스트 유지 (필요 시 별도 수정)
                    member.isChatAble(),           // 기존 chatAble 유지
                    requestDto.latitude(),          // 추가 정보: 위도
                    requestDto.longitude()          // 추가 정보: 경도
                );
                redisGeoService.addLocation(member.getId(), member.getLatitude(), member.getLongitude());
                member.updateRole(Role.ROLE_USER);
                return MemberInfoDto.from(member);
            } else {
                throw new GlobalException(GlobalErrorCode.DUPLICATE_MEMBER);
            }
        }
    }

    // 회원 정보 수정
    @Transactional
    public MemberResponseDto modifyMember(Long memberId, MemberModifyRequestDto requestDto) {
        Member member = getMemberEntity(memberId);

        member.updateProfile(
                requestDto.nickname(),
                requestDto.age(),
                requestDto.height(),
                requestDto.gender(),
                requestDto.images(),
                member.isChatAble(),
                // 사용자의 위도, 경도 값을 수정하여 위치 최신화
                requestDto.latitude() != null ? requestDto.latitude() : member.getLatitude(),
                requestDto.longitude() != null ? requestDto.longitude() : member.getLongitude()
        );
        return MemberResponseDto.from(member);
    }

    // 위치 정보 갱신 (프론트에서 버튼을 누르면 사용자 위치정보 최신화하여 갱신)
    @Transactional
    public MemberResponseDto updateLocation(Long memberId, Double latitude, Double longitude) {
        Member member = getMemberEntity(memberId);
        member.updateProfile(
                member.getNickname(),
                member.getAge(),
                member.getHeight(),
                member.getGender(),
                member.getImages(),
                member.isChatAble(),
                latitude,
                longitude
        );

        return MemberResponseDto.from(member);
    }

    // 회원 탈퇴 처리
    @Transactional
    public MemberResponseDto withdraw(Long memberId) {
        Member member = getMemberEntity(memberId);
        member.withdraw();

        // redis내에서 회원 위치 정보삭제
        redisGeoService.removeLocation(memberId);

        return MemberResponseDto.from(member);
    }

    // 닉네임 중복 검사
    public boolean isNicknameTaken(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    // 카카오 ID 기반 조회
    public Member getByKakaoId(Long kakaoId) {
        return memberRepository.findByKakaoIdAndIsDeletedFalse(kakaoId)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.MEMBER_NOT_FOUND));
    }

    // (탈퇴회원 제외)회원 PK 기준 Entity 조회 (공통 메서드)
    public Member getMemberEntity(Long memberId) {
        return memberRepository.findByIdAndIsDeletedFalse(memberId)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.MEMBER_NOT_FOUND));
    }

    // 멤버 전환 메서드
    @Transactional
    public void upgradeToUserRole(Long memberId) {
        Member member = getMemberEntity(memberId);
        if (member.getRole() == Role.ROLE_TEMP_USER) {
            member.updateRole(Role.ROLE_USER);
        }
    }

}
