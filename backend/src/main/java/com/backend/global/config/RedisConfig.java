package com.backend.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories
public class RedisConfig {

    // application.yml 파일에서 Redis 호스트 정보 주입
    @Value("${spring.data.redis.host}")
    private String redisHost;

    // application.yml 파일에서 Redis 포트 정보 주입
    @Value("${spring.data.redis.port}")
    private int redisPort;

    // Redis 접속을 위한 연결 팩토리 Bean 등록 (Lettuce 사용)
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(redisHost, redisPort);
    }

    // RedisTemplate 등록 - Redis와 상호작용할 수 있는 템플릿 객체
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }
}
