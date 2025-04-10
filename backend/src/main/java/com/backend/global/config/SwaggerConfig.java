package com.backend.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SwaggerConfig
 * 4차 프로젝트 March 6팀 API 문서 설정
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("4차 프로젝트 6팀 March API")
                        .description("6팀 March의 백엔드 API 명세서입니다.")
                        .version("v1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList("cookieAuth"))
                .addSecurityItem(new SecurityRequirement().addList("kakaoOAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("cookieAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.COOKIE)
                                .name("accessToken"))
                        .addSecuritySchemes("kakaoOAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.OAUTH2)
                                .flows(new OAuthFlows()
                                        .authorizationCode(new OAuthFlow()
                                                .authorizationUrl("https://kauth.kakao.com/oauth/authorize")
                                                .tokenUrl("https://kauth.kakao.com/oauth/token")
                                                .scopes(new Scopes()
                                                        .addString("profile_nickname", "사용자 닉네임")
//                                                        .addString("profile_image", "프로필 이미지")
                                                        .addString("account_email", "이메일"))))));
    }

    // 전체 API
    @Bean
    public GroupedOpenApi baseApi() {
        return GroupedOpenApi.builder()
                .group("Total Api")
                .pathsToMatch("/**")
                .build();
    }

    // 멤버 도메인
    @Bean
    public GroupedOpenApi memberApi() {
        return GroupedOpenApi.builder()
                .group("Member")
                .pathsToMatch("/api/members/**")
                .build();
    }

    // 인증 도메인
    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("Auth")
                .pathsToMatch("/api/auth/**")
                .build();
    }

    // 관리자 API
    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("Admin")
                .pathsToMatch("/admin/**")
                .build();
    }

    // 이미지 API
    @Bean
    public GroupedOpenApi imageApi() {
        return GroupedOpenApi.builder()
                .group("Image")
                .pathsToMatch("/api/images/**")
                .build();
    }

    // 좋아요 API
    @Bean
    public GroupedOpenApi likeApi() {
        return GroupedOpenApi.builder()
                .group("Like")
                .pathsToMatch("/api/likes/**")
                .build();
    }

    // 알림 API
    @Bean
    public GroupedOpenApi notificationApi() {
        return GroupedOpenApi.builder()
                .group("Notification")
                .pathsToMatch("/api/notifications/**")
                .build();
    }

    // 차단회원 API
    @Bean
    public GroupedOpenApi blockUserApi() {
        return GroupedOpenApi.builder()
                .group("BlockUser")
                .pathsToMatch("/api/block-user", "/api/unblock-user")
                .build();
    }

    // 회원추천 API
    @Bean
    public GroupedOpenApi matchingApi() {
        return GroupedOpenApi.builder()
                .group("UserRecommendation")
                .pathsToMatch("/api/matching/**")
                .build();
    }

    // 채팅요청 API
    @Bean
    public GroupedOpenApi chatRequestApi() {
        return GroupedOpenApi.builder()
                .group("ChatRequest")
                .pathsToMatch("/api/chat-request/**")
                .build();
    }

    // 키워드 API
    @Bean
    public GroupedOpenApi keywordApi() {
        return GroupedOpenApi.builder()
                .group("Keyword")
                .pathsToMatch("/api/keywords")
                .build();
    }

    // 유저 키워드 API
    @Bean
    public GroupedOpenApi userKeywordApi() {
        return GroupedOpenApi.builder()
                .group("UserKeyword")
                .pathsToMatch("/api/user-keywords/**")
                .build();
    }

    // 채팅 API
    @Bean
    public GroupedOpenApi chatApi() {
        return GroupedOpenApi.builder()
                .group("Chat")
                .pathsToMatch("/api/chatrooms/**")
                .build();
    }
}
