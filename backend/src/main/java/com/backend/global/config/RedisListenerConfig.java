package com.backend.global.config;

import com.backend.domain.chat.dto.ChatMessage;
import com.backend.domain.chat.service.redis.RedisSubscriber;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RedisListenerConfig {

    // Redis Pub/Sub에서 사용할 채널명 상수
    public static final String CHANNEL = "chat-message";

    private final RedisConnectionFactory redisConnectionFactory;
    private final ObjectMapper objectMapper;

    /**
     * Redis 메시지 수신을 위한 리스너 컨테이너 빈을 등록합니다.
     * - Redis 채널로부터 수신한 메시지를 JSON으로 파싱 후 처리합니다.
     * - 직접 message listener 구현 (람다식)으로 JSON 디코딩 포함
     *
     * @param connectionFactory Redis 연결 팩토리
     * @param redisSubscriber Redis 메시지 처리 클래스
     * @return RedisMessageListenerContainer 빈
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            @Lazy RedisSubscriber redisSubscriber) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);


        // 커스텀 MessageListener 사용해서 직접 JSON 파싱
        container.addMessageListener((message, pattern) -> {
            String msgBody = "";

            try {
                msgBody = new String(message.getBody());
                log.info("📩 Redis 수신 원문: {}", msgBody);

                // JSON 문자열을 ChatMessage 객체로 변환
                ChatMessage chatMessage = objectMapper.readValue(msgBody, ChatMessage.class);

                // RedisSubscriber를 통해 WebSocket 브로드캐스팅
                redisSubscriber.handleMessage(chatMessage);

            } catch (Exception e) {
                log.error("❌ Redis 메시지 파싱 실패: {}", msgBody, e);
            }
        }, new ChannelTopic(CHANNEL));

        return container;
    }

    /**
     * Redis 문자열 기반 템플릿 빈 등록
     * - Redis에 문자열 데이터를 저장하거나 발행할 때 사용
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }
}
