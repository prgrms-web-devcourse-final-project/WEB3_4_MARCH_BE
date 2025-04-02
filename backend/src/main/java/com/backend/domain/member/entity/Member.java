package com.backend.domain.member.entity;

import java.util.List;

import com.backend.domain.image.entity.Image;
import com.backend.domain.member.dto.MemberRegisterRequestDto;
import com.backend.global.base.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
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
    private Boolean chatAble = true;

    private Double latitude;

    private Double longitude;

    @Column(name = "kakao_access_token")
    private String kakaoAccessToken;

    @Column(name = "kakao_refresh_token")
    private String kakaoRefreshToken;

    // Entity에 탈퇴 여부 필드 추가 (isDeleted)
    // 회원 탈퇴(soft delete)
    @Column(nullable = false)
    private boolean isDeleted = false;

    @Builder
    public Member(Long kakaoId, String email, String nickname,
                  Integer age, Integer height, String gender,
                  List<Image> images, Boolean chatAble,
                  Double latitude, Double longitude,
                  String kakaoAccessToken, String kakaoRefreshToken) {

        this.kakaoId = kakaoId;
        this.email = email;
        this.nickname = nickname;
        this.age = age;
        this.height = height;
        this.gender = gender;
        this.images = images;
        this.chatAble = chatAble;
        this.latitude = latitude;
        this.longitude = longitude;
        this.kakaoAccessToken = kakaoAccessToken;
        this.kakaoRefreshToken = kakaoRefreshToken;
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

    public void updateAccessToken(String accessToken) {
        this.kakaoAccessToken = accessToken;

    }
    public void updateRefreshToken(String refreshToken) {
        this.kakaoRefreshToken = refreshToken;
    }

    // 회원 탈퇴(soft delete)
    public void withdraw() {
        this.isDeleted = true;
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

    public void setProfileImage(Image image) {
        this.profileImage = image;
    }
}
