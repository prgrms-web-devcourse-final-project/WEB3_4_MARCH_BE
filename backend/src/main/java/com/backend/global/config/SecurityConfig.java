package com.backend.global.config;

import com.backend.global.auth.jwt.JwtFilter;
import com.backend.global.response.GenericResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


/**
 * SecurityConfig
 * 시큐리티 관련 설정 클래스
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final ObjectMapper objectMapper;
    private final JwtFilter jwtFilter;
    private final CustomOAuth2AuthenticationSuccessHandler customOAuth2AuthenticationSuccessHandler;
    private final CustomAuthorizationRequestResolver customAuthorizationRequestResolver;

    // 인증 매니저를 빈으로 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // 핵심 보안 설정 (필터 체인 구성)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        auth -> auth
                                // 관리자 전용 URL은 ROLE_ADMIN 권한 필요
                                .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
                                .requestMatchers("/api/members/me").hasAnyAuthority("ROLE_USER", "ROLE_TEMP_USER", "ROLE_ADMIN")
                                .requestMatchers(
                                        "/",
                                        "/actuator/**",
//                                "https://connect-to.pages.dev/",
//                                "https://connect-to.pages.dev",
                                        "/api/auth/**",
                                        "/swagger-ui/**",
                                        "/swagger-ui.html",
                                        "/v3/api-docs/**",
                                        "/swagger-resources/**",
                                        "/webjars/**",
                                        "/api/members/register",
                                        "/chat-test.html",
                                        "/page-list-test.html",
                                        "/js/**",
                                        "/ws/**",
                                        "/favicon.ico",
                                        "/error"
                                ).permitAll()
                                .anyRequest()
                                .authenticated()
                )
                .oauth2Login(
                        oauth2Login -> oauth2Login
                                .successHandler(customOAuth2AuthenticationSuccessHandler)
                                .authorizationEndpoint(
                                        authorizationEndpoint ->
                                                authorizationEndpoint
                                                        .authorizationRequestResolver(customAuthorizationRequestResolver)
                                )
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(
                        exception -> exception
                                .authenticationEntryPoint((request, response, authException) -> {
                                    response.setContentType("application/json;charset=UTF-8");
                                    response.setStatus(401);
                                    response.getWriter().write(
                                            objectMapper.writeValueAsString(
                                                    GenericResponse.fail(
                                                            HttpStatus.UNAUTHORIZED.value(),
                                                            "Unauthorized",
                                                            "인증되지 않은 사용자입니다."
                                                    )
                                            )
                                    );
                                })
                                .accessDeniedHandler((request, response, accessDeniedException) -> {
                                    response.setContentType("application/json;charset=UTF-8");
                                    response.setStatus(403);
                                    response.getWriter().write(
                                            objectMapper.writeValueAsString(
                                                    GenericResponse.fail(
                                                            HttpStatus.FORBIDDEN.value(),
                                                            "Forbidden",
                                                            "권한이 없습니다."
                                                    )
                                            )
                                    );
                                })
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "https://connect-to.pages.dev",
                "https://connect-to.pages.dev/",
                "https://api.connect-to.shop",  // EC2 서버 도메인
                "https://www.connect-to.shop"
        )); // 프론트 주소
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true); // 쿠키 전송 허용
        config.setExposedHeaders(List.of("Authorization")); // 필요한 경우 expose

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}

