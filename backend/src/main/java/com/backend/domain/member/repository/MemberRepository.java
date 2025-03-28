package com.backend.domain.member.repository;

import com.backend.domain.member.entity.Member;
import com.backend.domain.member.dto.MemberInfoDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.List;

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
    @Query("SELECT new com.backend.domain.member.dto.MemberInfoDto(" +
            "m.id, m.kakaoId, m.email, m.nickname, m.age, m.height, m.gender, " +
            "m.chatAble, m.profileImage, m.latitude, m.longitude) " +
            "FROM Member m WHERE m.id = :id")
    Optional<MemberInfoDto> findMemberInfoDtoById(Long id);

    // 닉네임 유사 검색 (회원 리스트)
    @Query("SELECT new com.backend.domain.member.dto.MemberInfoDto(" +
            "m.id, m.kakaoId, m.email, m.nickname, m.age, m.height, m.gender, " +
            "m.chatAble, m.profileImage, m.latitude, m.longitude) " +
            "FROM Member m WHERE m.nickname LIKE %:nickname%")
    List<MemberInfoDto> findMemberInfoDtosByNickname(String nickname);
}
