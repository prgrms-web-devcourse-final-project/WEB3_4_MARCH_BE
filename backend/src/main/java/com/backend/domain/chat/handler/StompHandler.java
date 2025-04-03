package com.backend.domain.chat.handler;

import com.backend.global.auth.kakao.util.JwtUtil;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * StompHandler는 WebSocket 연결 시 STOMP 프로토콜 메시지를 인터셉트하여
 * JWT 토큰을 검증하고, SecurityContext에 인증 정보를 설정하는 역할을 합니다.
 */
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

    /**
     * STOMP 메시지가 전송되기 전에 호출됩니다.
     * 주로 CONNECT 커맨드일 때 JWT 토큰을 검증하여 SecurityContext에 인증 정보를 설정합니다.
     *
     * @param message  클라이언트가 전송한 STOMP 메시지
     * @param channel  메시지가 전달되는 채널
     * @return 검증 및 처리가 완료된 메시지
     * @throws GlobalException 인증 토큰이 없거나 유효하지 않은 경우 예외 발생
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // STOMP 헤더 정보를 쉽게 다루기 위해 헤더 접근 객체 생성
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // CONNECT 커맨드일 경우에만 JWT 토큰 검증을 진행합니다.
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            // 클라이언트가 보낸 Authorization 헤더에서 토큰을 추출
            String token = accessor.getFirstNativeHeader("Authorization");

            // 토큰이 없거나 "Bearer " 형식이 아닌 경우 예외 발생
            if (token == null || !token.startsWith("Bearer ")) {
                throw new GlobalException(GlobalErrorCode.TOKEN_NOT_FOUND);
            }

            // "Bearer " 접두어를 제거하여 실제 토큰 값만 추출
            token = token.substring(7);

            try {
                // 토큰 유효성 검사
                if (jwtUtil.validateToken(token)) {
                    // 토큰이 유효하면, 토큰에서 Authentication 객체를 추출
                    Authentication authentication = jwtUtil.getAuthentication(token);
                    // SecurityContext에 인증 정보를 설정
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    // 토큰이 유효하지 않은 경우 예외 발생
                    throw new GlobalException(GlobalErrorCode.INVALID_TOKEN);
                }
            } catch (Exception e) {
                // 토큰 처리 중 오류가 발생하면 예외 발생
                throw new GlobalException(GlobalErrorCode.INVALID_TOKEN);
            }
        }
        // 최종적으로 처리된 메시지를 반환
        return message;
    }
}
