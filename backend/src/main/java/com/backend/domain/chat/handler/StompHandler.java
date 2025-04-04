package com.backend.domain.chat.handler;

import com.backend.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

/**
 * StompHandler는 WebSocket 연결 시 STOMP 프로토콜 메시지를 인터셉트하여
 * JWT 토큰을 검증하고, SecurityContext에 인증 정보를 설정하는 역할을 합니다.
 */
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

//    private final JwtUtil jwtUtil;

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
        return message;
    }

        /*
        // 기존 JWT 토큰 검증 로직
        String token = accessor.getFirstNativeHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            throw new GlobalException(GlobalErrorCode.TOKEN_NOT_FOUND);
        }

        token = token.substring(7);

        try {
            if (jwtUtil.validateToken(token)) {
                Authentication authentication = jwtUtil.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                throw new GlobalException(GlobalErrorCode.INVALID_TOKEN);
            }
        } catch (Exception e) {
            throw new GlobalException(GlobalErrorCode.INVALID_TOKEN);
        }
        */
//        }
//        // 최종적으로 처리된 메시지를 반환
//        return message;
//    }
}
