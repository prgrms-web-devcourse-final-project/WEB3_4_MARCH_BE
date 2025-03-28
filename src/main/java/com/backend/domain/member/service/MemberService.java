package com.backend.domain.member.service;

import com.backend.domain.member.dto.MemberInfoDto;
import com.backend.domain.member.dto.MemberModifyRequestDto;
import com.backend.domain.member.dto.MemberRegisterRequestDto;
import com.backend.domain.member.dto.MemberResponseDto;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.exception.MemberErrorCode;
import com.backend.domain.member.exception.MemberException;
import com.backend.domain.member.repository.MemberRepository;
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

    // 회원 정보 조회
    // Member 엔티티를 DTO로 변환해서 반환
    @Transactional(readOnly = true)
    public MemberInfoDto getMemberInfo(Long memberId) {
        return memberRepository.findById(memberId)
                .filter(member -> !member.isDeleted())
                .map(MemberInfoDto::from)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    // 닉네임으로 조회
    public List<MemberInfoDto> searchByNickname(String nickname) {
        return memberRepository.findByNicknameContaining(nickname).stream()
                .filter(member -> !member.isDeleted())
                .map(MemberInfoDto::from)
                .collect(Collectors.toList());
    }

    // 회원 가입 처리 (카카오 로그인 이후)
    // 재가입 허용 처리 포함
    @Transactional
    public MemberInfoDto registerMember(MemberRegisterRequestDto requestDto) {
        // 1. 기존 활성 회원 여부
        if (memberRepository.existsByKakaoIdAndIsDeletedFalse(requestDto.kakaoId())) {
            throw new MemberException(MemberErrorCode.DUPLICATE_MEMBER);
        }

        // 2. 탈퇴한 회원이면 복구
        Member member = memberRepository.findByKakaoId(requestDto.kakaoId()).map(existing -> {
            if (existing.isDeleted()) {
                existing.rejoin(requestDto);
                return existing;
            } else {
                throw new MemberException(MemberErrorCode.DUPLICATE_MEMBER);
            }
        }).orElseGet(() -> {
            // 3. 신규 등록
            Member newMember = Member.builder()
                    .kakaoId(requestDto.kakaoId())
                    .email(requestDto.email())
                    .nickname(requestDto.nickname())
                    .age(requestDto.age())
                    .height(requestDto.height())
                    .gender(requestDto.gender())
                    .profileImage(requestDto.profileImage())
                    .chatAble(true)
                    .latitude(requestDto.latitude())
                    .longitude(requestDto.longitude())
                    .kakaoAccessToken(null)
                    .kakaoRefreshToken(null)
                    .build();
            return memberRepository.save(newMember);
        });

        return MemberInfoDto.from(member);
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
                requestDto.profileImage(),
                member.getChatAble(),
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
                member.getProfileImage(),
                member.getChatAble(),
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
        return MemberResponseDto.from(member);
    }

    // 닉네임 중복 검사
    public boolean isNicknameTaken(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    // 카카오 ID 기반 조회
    public Member getByKakaoId(Long kakaoId) {
        return memberRepository.findByKakaoIdAndIsDeletedFalse(kakaoId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    // (탈퇴회원 제외)회원 PK 기준 Entity 조회 (공통 메서드)
    public Member getMemberEntity(Long memberId) {
        return memberRepository.findByIdAndIsDeletedFalse(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

}
