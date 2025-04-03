//package com.backend.domain.chat.handler;
//
//import com.backend.global.auth.kakao.util.JwtUtil;
//import com.backend.global.exception.GlobalErrorCode;
//import com.backend.global.exception.GlobalException;
//import lombok.RequiredArgsConstructor;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.simp.stomp.StompCommand;
//import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
//import org.springframework.messaging.support.ChannelInterceptor;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class StompHandler implements ChannelInterceptor {
//
//    private final JwtUtil jwtUtil;
//
//    @Override
//    public Message<?> preSend(Message<?> message, MessageChannel channel) {
//        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
//
//        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
//            String token = accessor.getFirstNativeHeader("Authorization");
//
//            if (token == null || !token.startsWith("Bearer ")) {
//
//                // 헤더가 없거나, 잘못된 형식이면 에러 발생
//                throw new GlobalException(GlobalErrorCode.TOKEN_NOT_FOUND);
//            }
//
//            token = token.substring(7);
//
//            try {
//                if (jwtUtil.validateToken(token)) {
//                    Authentication authentication = jwtUtil.getAuthentication(token);
//                    SecurityContextHolder.getContext().setAuthentication(authentication);
//                } else {
//                    throw new GlobalException(GlobalErrorCode.INVALID_TOKEN);
//                }
//            } catch (Exception e) {
//                throw new GlobalException(GlobalErrorCode.INVALID_TOKEN);
//            }
//        }
//        return message;
//    }
//}
