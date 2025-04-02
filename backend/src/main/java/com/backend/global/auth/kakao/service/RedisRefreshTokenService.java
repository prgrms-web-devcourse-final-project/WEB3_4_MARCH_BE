package com.backend.global.auth.kakao.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Redis를 활용하여 리프레시 토큰을 저장, 검증, 삭제하는 서비스
 * - 저장 시 만료시간을 함께 설정함
 * - 저장 키는 "RT:{memberId}" 형식으로 구성됨
 */

@Service
@RequiredArgsConstructor
public class RedisRefreshTokenService {

    private final StringRedisTemplate redisTemplate;

    /**
     * 리프레시 토큰을 Redis에 저장
     * @param memberId 사용자 ID
     * @param token 저장할 리프레시 토큰
     * @param expiration 토큰 만료시간 (밀리초)
     */
    public void saveRefreshToken(Long memberId, String token, long expiration) {
        redisTemplate.opsForValue().set(getKey(memberId), token, Duration.ofMillis(expiration));
    }

    /**
     * 저장된 리프레시 토큰이 유효한지 검증
     * @param memberId 사용자 ID
     * @param token 요청에서 받은 리프레시 토큰
     * @return 토큰이 일치하면 true
     */
    public boolean isValid(Long memberId, String token) {
        String storedToken = redisTemplate.opsForValue().get(getKey(memberId));
        return storedToken != null && storedToken.equals(token);
    }

    /**
     * Redis에서 리프레시 토큰 삭제
     * @param memberId 사용자 ID
     */
    public void deleteRefreshToken(Long memberId) {
        redisTemplate.delete(getKey(memberId));
    }

    /**
     * Redis에 저장될 키 형식 생성
     * @param memberId 사용자 ID
     * @return "RT:{memberId}" 형식의 키
     */
    private String getKey(Long memberId) {
        return "RT:" + memberId;
    }
}
