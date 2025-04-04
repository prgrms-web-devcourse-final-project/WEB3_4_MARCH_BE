package com.backend.global.config;

import com.backend.global.auth.interceptor.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebConfig
 * Spring MVC 설정 클래스
 * - 사용자 정의 인터셉터(AuthInterceptor)를 Spring MVC 요청 흐름에 등록한다.
 * - 주로 인증 이후 사용자 흐름 제어(예: ROLE_TEMP_USER 차단 등)에 활용된다.
 * - Spring Security의 보안 설정(SecurityConfig)과는 별도로 동작한다.
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/auth/**",        // 인증 관련 예외 처리 경로
                        "/api/members/register", // 추가 정보 등록 허용 경로
                        "/error",              // 에러 처리 경로
                        "/swagger-ui/**",      // Swagger 경로 (선택)
                        "/v3/api-docs/**"       // Swagger Docs 경로 (선택)
                );
    }
}
