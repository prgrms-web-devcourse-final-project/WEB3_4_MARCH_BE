package com.backend.test.domain.member;

import com.backend.domain.member.dto.MemberResponseDto;
import com.backend.test.global.util.TestTokenProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Import(MemberControllerTest.TestConfig.class)
public class MemberControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public com.backend.domain.member.service.MemberService memberService() {
            return Mockito.mock(com.backend.domain.member.service.MemberService.class);
        }

        @Bean
        public com.backend.domain.userkeyword.service.UserKeywordService userKeywordService() {
            return Mockito.mock(com.backend.domain.userkeyword.service.UserKeywordService.class);
        }

        @Bean
        public com.backend.domain.image.service.PresignedService presignedService() {
            return Mockito.mock(com.backend.domain.image.service.PresignedService.class);
        }

        @Bean
        public com.backend.global.auth.kakao.service.CookieService cookieService() {
            return Mockito.mock(com.backend.global.auth.kakao.service.CookieService.class);
        }

        @Bean
        public com.backend.global.auth.kakao.util.TokenProvider tokenProvider() {
            return Mockito.mock(com.backend.global.auth.kakao.util.TokenProvider.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private com.backend.domain.member.service.MemberService memberService;

    @Autowired
    TestTokenProvider testTokenProvider;

    private String token;

    @BeforeEach
    void setUp() {
        token = testTokenProvider.generateTestAccessToken(1L, "ROLE_USER");

        var loginUser = new com.backend.global.auth.model.CustomUserDetails(
                1L,
                "test@example.com",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        SecurityContextHolder.getContext().setAuthentication(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        loginUser, null, loginUser.getAuthorities()
                )
        );
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("내 프로필 조회 성공")
    void getMyProfile_success() throws Exception {
        Mockito.when(memberService.getMemberInfo(any(), eq(1L)))
                .thenReturn(dummyResponse(1L, "진원"));

        mockMvc.perform(get("/api/members/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("다른 회원 프로필 조회 성공")
    void getMemberProfile_success() throws Exception {
        Mockito.when(memberService.getMemberInfo(any(), eq(2L)))
                .thenReturn(dummyResponse(2L, "다른사람"));

        mockMvc.perform(get("/api/members/{memberId}", 2L)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("닉네임 검색 성공")
    void searchMembersByNickname_success() throws Exception {
        Mockito.when(memberService.searchByNickname("jin"))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/members/search")
                        .param("nickname", "jin")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("닉네임 중복 검사 성공")
    void checkNickname_available() throws Exception {
        Mockito.when(memberService.isNicknameTaken("newuser")).thenReturn(false);

        mockMvc.perform(get("/api/members/checkNickname")
                        .param("nickname", "newuser"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("회원 위치 수정 성공")
    void updateLocation_success() throws Exception {
        Mockito.when(memberService.updateLocation(eq(1L), anyDouble(), anyDouble()))
                .thenReturn(dummyResponse(1L, "위치유저"));

        mockMvc.perform(patch("/api/members/{memberId}/location", 1L)
                        .param("latitude", "36.0")
                        .param("longitude", "128.0")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("회원 탈퇴 성공")
    void withdrawMember_success() throws Exception {
        Mockito.when(memberService.withdraw(1L))
                .thenReturn(dummyResponse(1L, "탈퇴유저", true));

        mockMvc.perform(delete("/api/members/{memberId}", 1L)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    private MemberResponseDto dummyResponse(Long id, String nickname) {
        return dummyResponse(id, nickname, false);
    }

    private MemberResponseDto dummyResponse(Long id, String nickname, boolean isDeleted) {
        return new MemberResponseDto(
                id, nickname, "MALE", 28, 180, null, List.of(),
                "소개", List.of(), false, null, false, isDeleted,
                36.0, 128.0, "ROLE_USER"
        );
    }
}
