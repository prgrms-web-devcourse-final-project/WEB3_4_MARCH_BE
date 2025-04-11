package com.backend.domain.member.service;

import com.backend.domain.chatrequest.dto.response.ChatRequestDto;
import com.backend.domain.chatrequest.entity.ChatRequestStatus;
import com.backend.domain.chatrequest.service.ChatRequestService;
import com.backend.domain.image.service.ImageService;
import com.backend.domain.image.service.PresignedService;
import com.backend.domain.keyword.entity.Keyword;
import com.backend.domain.like.service.LikeService;
import com.backend.domain.member.dto.MemberInfoDto;
import com.backend.domain.member.dto.MemberModifyRequestDto;
import com.backend.domain.member.dto.MemberRegisterRequestDto;
import com.backend.domain.member.dto.MemberResponseDto;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.entity.Role;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.domain.userkeyword.dto.response.UserKeywordResponse;
import com.backend.domain.userkeyword.service.UserKeywordService;
import com.backend.global.auth.model.CustomUserDetails;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import com.backend.global.redis.service.RedisGeoService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final RedisGeoService redisGeoService;
    private final ImageService imageService;
    private final PresignedService presignedService;
    private final UserKeywordService userKeywordService;
    private final LikeService likeService;
    private final ChatRequestService chatRequestService;

    // 회원 프로필 정보 조회
    // 서비스 내부 조회/리스트 반환용 (Service → API/Query)
    @Transactional(readOnly = true)
    public MemberInfoDto getMemberInfoForInternal(Long memberId) {
        Member member = getMemberEntity(memberId);

        List<UserKeywordResponse> keywords = userKeywordService.getUserKeywords(memberId);
        return MemberInfoDto.from(member, keywords);
    }

    // 다른 회원 프로필 정보 조회 (API 응답 전용 DTO, Controller → Client)
    // Member 엔티티를 DTO로 변환해서 반환
    @Transactional(readOnly = true)
    public MemberResponseDto getMemberInfo(CustomUserDetails loginMember, Long memberId) {
        Member member = getMemberEntity(memberId);

        // 내가 이 회원을 좋아요 했는지 여부
        boolean liked = false;
        // 채팅 요청 상태 (보낸 적 없다면 null, 나머지는 ChatRequestStatus 상태로 보여짐)
        ChatRequestStatus chatRequestStatus = null;

        // 동적 필드: 로그인 유저와 다른 유저일 경우에만 계산
        if (!loginMember.equals(memberId)) {
            liked = likeService.getLikesGiven(loginMember.getMemberId()).stream()
                    .anyMatch(like -> like.getReceiverId().equals(memberId));

            chatRequestStatus = chatRequestService.getSentRequests(loginMember).stream()
                    .filter(req -> req.getReceiverId().equals(memberId))
                    .map(ChatRequestDto::getStatus)
                    .findFirst()
                    .orElse(null);  // 요청 없으면 null
        }

        // 키워드 리스트
        List<UserKeywordResponse> keywords = userKeywordService.getUserKeywords(memberId);

        // 응답 DTO 생성하여 반환
        return MemberResponseDto.from(member, keywords, liked, chatRequestStatus);

    }

    // 닉네임으로 조회
    public List<MemberResponseDto> searchByNickname(String nickname) {
        return memberRepository.findByNicknameContaining(nickname).stream()
                .filter(member -> !member.isDeleted())
                .map(member -> {
                    List<UserKeywordResponse> keywords = userKeywordService.getUserKeywords(member.getId());
                    return MemberResponseDto.from(member, keywords, false, null);
                })
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
    @Transactional(rollbackFor = Exception.class)
    public MemberInfoDto registerMember(MemberRegisterRequestDto requestDto,
                                        List<MultipartFile> imageFiles,
                                        List<Long> keywordIds,
                                        HttpServletResponse response) throws IOException {
        try {
            // 1. 기존 활성 회원 여부

            Member member = memberRepository.findByKakaoId(requestDto.kakaoId())
                    .orElseThrow(() -> new GlobalException(GlobalErrorCode.MEMBER_NOT_FOUND));

            // 2. 탈퇴한 회원이면 복구
            if (member.isDeleted()) {
                member.rejoin(requestDto);
                member.updateRole(Role.ROLE_USER);
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
                            requestDto.longitude(),          // 추가 정보: 경도
                            requestDto.introduction()
                    );
                    redisGeoService.addLocation(member.getId(), member.getLatitude(), member.getLongitude());
                } else {
                    throw new GlobalException(GlobalErrorCode.DUPLICATE_MEMBER);
                }
            }

            // 키워드 리스트 포함해서 DTO 반환
            List<UserKeywordResponse> keywords = userKeywordService.getUserKeywords(member.getId());
            return MemberInfoDto.from(member, keywords);
        } catch (Exception e) {
            // 트랜잭션 내 예외가 발생하면 롤백 유도
            throw new GlobalException(GlobalErrorCode.MEMBER_REGISTRATION_FAILED, "회원가입 중 예외 발생");
        }
    }

    @Transactional
    public void setRole(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(
                () -> new GlobalException(GlobalErrorCode.MEMBER_NOT_FOUND)
        );

        member.updateRole(Role.ROLE_USER);
    }

    // 회원 정보 수정
    @Transactional
    public MemberResponseDto modifyMember(Long memberId,
                                          MemberModifyRequestDto dto,
                                          List<Long> keepImageIds,
                                          List<MultipartFile> newImages) throws IOException {

        Member member = getMemberEntity(memberId);

        // 1. 기존 이미지 삭제 로직
        member.getImages().stream()
                .filter(img -> !keepImageIds.contains(img.getId()))
                .toList()
                .forEach(img -> imageService.deleteImage(memberId, img.getId()));

        // 2. 새 이미지 추가 로직
        int finalCount = keepImageIds.size() + (newImages == null ? 0 : newImages.size());
        if (finalCount < 1 || finalCount > 5) {
            throw new GlobalException(GlobalErrorCode.IMAGE_COUNT_INVALID);
        }
        if (newImages != null && !newImages.isEmpty()) {
            presignedService.uploadFiles(newImages, memberId);
        }

        // 3. 프로필 정보 수정
        member.updateProfile(
                dto.nickname(),
                dto.age(),
                dto.height(),
                dto.gender(),
                member.getImages(),
                member.isChatAble(),
                dto.latitude() != null ? dto.latitude() : member.getLatitude(),
                dto.longitude() != null ? dto.longitude() : member.getLongitude(),
                dto.introduction()
        );

        // 4. 키워드 수정
        if (dto.keywords() != null && !dto.keywords().isEmpty()) {
            // Keyword 엔티티 리스트를 받아 처리
            List<Long> keywordIds = dto.keywords().stream()
                    .map(Keyword::getId)
                    .toList();
            userKeywordService.updateUserKeywords(memberId, keywordIds);
        }

        // 5. 최신 키워드 조회 및 응답 DTO 반환
        List<UserKeywordResponse> keywords = userKeywordService.getUserKeywords(memberId);

        // 팩토리 메서드로 응답 DTO 반환
        return MemberResponseDto.from(member, keywords, false, null);
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
                longitude,
                member.getIntroduction()
        );

        // 키워드 리스트 포함해서 DTO 반환
        List<UserKeywordResponse> keywords = userKeywordService.getUserKeywords(memberId);

        return MemberResponseDto.from(member, keywords, false, null);
    }

    // 회원 탈퇴 처리
    @Transactional
    public MemberResponseDto withdraw(Long memberId) {
        Member member = getMemberEntity(memberId);
        member.withdraw(); // → isDeleted = true, status = WITHDRAWN

        // redis내에서 회원 위치 정보삭제
        redisGeoService.removeLocation(memberId);

        // 키워드 리스트 포함해서 DTO 반환
        List<UserKeywordResponse> keywords = userKeywordService.getUserKeywords(memberId);

        return MemberResponseDto.from(member, keywords, false, null);
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

    // 임시 멤버를 일반 멤버로 전환 메서드
    @Transactional
    public void upgradeToUserRole(Long memberId) {
        Member member = getMemberEntity(memberId);
        if (member.getRole() == Role.ROLE_TEMP_USER) {
            member.updateRole(Role.ROLE_USER);
        }
    }

}
