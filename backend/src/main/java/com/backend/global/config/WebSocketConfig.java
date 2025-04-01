package com.backend.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket 설정 클래스입니다.
 * - SockJS를 통한 WebSocket 연결 엔드포인트 등록
 * - 메시지 브로커 경로 설정 (STOMP 프로토콜 사용)
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * WebSocket 연결을 위한 STOMP 엔드포인트를 등록합니다.
     * - 클라이언트는 "/ws"로 연결
     * - withSockJS(): WebSocket 미지원 브라우저를 위한 폴백 옵션
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    /**
     * 메시지 브로커 구성을 설정합니다.
     * - enableSimpleBroker: 구독 경로 설정
     * - setApplicationDestinationPrefixes: 서버 수신 경로 설정
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/api/sub");
        config.setApplicationDestinationPrefixes("/api/pub"); // 구독 경로
    }
}
