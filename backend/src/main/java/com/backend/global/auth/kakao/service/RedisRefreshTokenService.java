package com.backend.global.auth.kakao.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Redis를 활용하여 리프레시 토큰과 클라이언트 식별정보(IP, User-Agent)를 저장, 검증, 삭제하는 서비스
 * - 저장 시 고유 식별자(jti)를 포함한 키("RT:{memberId}:{jti}")를 사용하여 각 토큰별로 관리합
 * - 토큰과 함께 해당 클라이언트의 IP, User-Agent 정보를 저장하여 이후 검증 시 활용할 수 있다.
 * - 저장 시 만료시간을 함께 설정하여 자동 만료되도록 한다.
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisRefreshTokenService {

    private final StringRedisTemplate redisTemplate;

    /**
     * @param memberId   사용자 ID
     * @param jti        JWT의 고유 식별자 (refreshToken의 jti)
     * @param token      저장할 리프레시 토큰
     * @param expiration 토큰 만료시간 (밀리초)
     * @param ip         클라이언트의 IP 주소
     * @param userAgent  클라이언트의 User-Agent
     */
    public void saveRefreshToken(Long memberId, String jti, String token, long expiration, String ip, String userAgent) {
        String key = getKey(memberId, jti);
        String value = token + "|" + ip + "|" + userAgent;
        redisTemplate.opsForValue().set(key, value, Duration.ofMillis(expiration));
    }

    /**
     * 저장된 리프레시 토큰과 클라이언트 정보를 검증
     *
     * @param memberId         사용자 ID
     * @param jti              JWT의 고유 식별자 (refreshToken의 jti)
     * @param token            요청에서 받은 리프레시 토큰
     * @param currentIp        현재 요청의 클라이언트 IP 주소
     * @param currentUserAgent 현재 요청의 클라이언트 User-Agent
     * @return 저장된 토큰, IP, User-Agent 정보가 요청 정보와 일치하면 true, 그렇지 않으면 false
     * 화이트리스트 방식: Redis에 허용된 refreshToken만 저장하고, 요청이 들어오면 이 토큰이 존재하는지 확인하는 구조
     */
    public boolean isValid(Long memberId, String jti, String token, String currentIp, String currentUserAgent) {
        String key = getKey(memberId, jti);
        String storedData = redisTemplate.opsForValue().get(key);

        if (storedData == null) {
            log.warn("[RedisRefreshTokenService] 키 {} 에 저장된 토큰이 존재하지 않습니다.", key);
            return false;
        }

        // storedData 형식: refreshToken|ip|userAgent
        String[] parts = storedData.split("\\|");
        if (parts.length != 3) {
            log.warn("[RedisRefreshTokenService] 키 {} 에 저장된 토큰 형식이 올바르지 않습니다.", key);
            return false;
        }

        String storedToken = parts[0];
        String storedIp = parts[1];
        String storedUserAgent = parts[2];

        if (!storedToken.equals(token)) {
            log.warn("[RedisRefreshTokenService] 키 {} 에서 토큰 불일치 발생. 저장된 토큰: {} / 제공된 토큰: {}", key, storedToken, token);
            return false;
        }
        if (!storedIp.equals(currentIp) || !storedUserAgent.equals(currentUserAgent)) {
            log.warn("[RedisRefreshTokenService] 키 {} 에서 클라이언트 정보 불일치 발생. 예상 IP/UserAgent: {}/{} / 실제 IP/UserAgent: {}/{}", key, storedIp, storedUserAgent, currentIp, currentUserAgent);
            return false;
        }
        return true;
    }


    /**
     * Redis에서 리프레시 토큰 삭제
     *
     * @param memberId 사용자 ID
     * @param jti      JWT의 고유 식별자 (refreshToken의 jti)
     */
    public void deleteRefreshToken(Long memberId, String jti) {
        redisTemplate.delete(getKey(memberId, jti));
    }

    /**
     * Redis에 저장될 키 형식 생성
     *
     * @param memberId 사용자 ID
     * @param jti      JWT의 고유 식별자
     * @return "RT:{memberId}:{jti}" 형식의 키
     */
    private String getKey(Long memberId, String jti) {
        return "RT:" + memberId + ":" + jti;
    }
}
