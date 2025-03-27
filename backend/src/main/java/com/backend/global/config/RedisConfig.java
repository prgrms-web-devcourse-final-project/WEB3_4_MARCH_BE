package com.backend.global.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class RedisConfig {

    /**
     * Redis와 연결을 위한 커넥션 팩토리 등록
     * (application.yml에 적은 host, port 값을 자동으로 읽어서 연결함)
     *
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(); // 기본 설정: localhost:6379
    }


    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // Redis 서버와 연결할때 사용할 커넥션 설정 주입
        template.setConnectionFactory(factory);
        // Redis의 Key는 문자열로 저장하겠다는 설정
        template.setKeySerializer(new StringRedisSerializer());
        // Redis의 Value는 JSON 형태로 직렬화/역직렬화 하겠다는 설정
        // -> 객체가 자동으로 JSON으로 변환되어 저장되고, 꺼낼 때 다시 객체로 복원됨
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer()); // JSON 형태로 저장
        return template;
    }




}
