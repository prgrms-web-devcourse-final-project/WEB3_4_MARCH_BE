package com.backend.domain.member.entity;

import com.backend.domain.image.entity.Image;
import com.backend.domain.member.dto.MemberRegisterRequestDto;
import com.backend.global.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 내부 고유 식별자

    @Column(unique = true, nullable = false)
    private Long kakaoId; // 카카오 식별자

    @Column(nullable = true)
    private String email; // 카카오 제공 이메일 (표시용)

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    private Integer height;

    @Column(nullable = false)
    private String gender;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Image> images;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_image_id")
    private Image profileImage;

    @Column(nullable = false)
    @Builder.Default
    private boolean chatAble = true;

    private Double latitude;

    private Double longitude;

    @Enumerated(EnumType.STRING)
    private Role role; // 사용자의 권한 종류

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MemberStatus status = MemberStatus.ACTIVE;


    // Entity에 탈퇴 여부 필드 추가 (isDeleted)
    // 회원 탈퇴(soft delete)
    @Column(nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

    // 카카오에서 받아온 데이터를 저장하는 정적 팩토리 메서드
    public static Member ofKakaoUser(Long kakaoId, String email, String nickname, Role role) {
        return Member.builder()
                .kakaoId(kakaoId)
                .email(email)
                .nickname(nickname)
                .age(0)
                .height(0)
                .gender("UNKNOWN")
                .chatAble(true)
                .role(role)
                .build();
    }

    public void updateProfile(String nickname, Integer age, Integer height, String gender,
                              List<Image> images, Boolean chatAble,
                              Double latitude, Double longitude) {
        this.nickname = nickname;
        this.age = age;
        this.height = height;
        this.gender = gender;
        this.images = images;
        this.chatAble = chatAble;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // 회원 탈퇴(soft delete)
    // 회원 탈퇴 시 상태 "WITHDRAWN"으로 변경
    public void withdraw() {
        this.isDeleted = true;
        this.status = MemberStatus.WITHDRAWN;;
    }

    // 관리자 차단 시 상태 변경 (getStatus() 메서드는 Lombok의 @Getter로 자동 생성됨)
    public void block() {
        this.status = MemberStatus.BLOCKED;
    }

    // 재가입 처리
    public void rejoin(MemberRegisterRequestDto requestDto) {
        this.email = requestDto.email();
        this.nickname = requestDto.nickname();
        this.age = requestDto.age();
        this.height = requestDto.height();
        this.gender = requestDto.gender();
        this.isDeleted = false;
    }

    // 멤버 권한 전환
    public void updateRole(Role role) {
        this.role = role;
    }

    public void setProfileImage(Image image) {
        this.profileImage = image;
    }

    // Lazy 로딩된 프록시 간의 비교 문제를 방지하기 위해, equals()와 hashCode()는 id 값만을 기준으로 비교합니다.
    // 이 방식은 실제 엔티티가 아닌 프록시 객체라도 동일한 id를 가진 경우 동일한 객체로 인식하게 합니다.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Member)) return false;
        Member member = (Member) o;
        return id != null && id.equals(member.getId());
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
