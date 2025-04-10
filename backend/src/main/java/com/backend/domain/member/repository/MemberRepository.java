package com.backend.domain.member.repository;

import com.backend.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 카카오 ID 기반 회원 조회
    Optional<Member> findByKakaoId(Long kakaoId);

    // 해당 카카오 ID로 등록된 회원 존재 여부
    boolean existsByKakaoId(Long kakaoId);

    // 닉네임 중복 검사
    boolean existsByNickname(String nickname);

    // 탈퇴 회원 제외
    Optional<Member> findByIdAndIsDeletedFalse(Long id);
    Optional<Member> findByKakaoIdAndIsDeletedFalse(Long kakaoId);
    boolean existsByKakaoIdAndIsDeletedFalse(Long kakaoId);

    // 회원 상세 정보 DTO 조회 (단건)
    // DTO 변환은 서비스에서 map(MemberInfoDto::from) 사용
    Optional<Member> findById(Long id);

    // 닉네임 검색 (회원 리스트)
    // DTO 변환은 서비스에서 map(MemberInfoDto::from) 사용
    List<Member> findByNicknameContaining(String nickname);

    // 이메일도 포함한 검색이 필요하면 아래 메서드를 추가
    Page<Member> findByNicknameContainingOrEmailContaining(String nickname, String email, Pageable pageable);

    // 이메일로 회원 조회 (관리자 로그인에 사용)
    Optional<Member> findByEmail(String email);
}
