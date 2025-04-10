package com.backend.domain.chat.service.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

/**
 * RedisUnreadService는 Redis를 사용하여 각 사용자의 채팅방 내 안 읽은 메시지 수를 관리하는 서비스입니다.
 * - 특정 채팅방의 수신자의 안 읽은 메시지 수를 증가, 조회, 초기화하는 기능을 제공합니다.
 */
@Service
@RequiredArgsConstructor
public class RedisUnreadService {

    private final StringRedisTemplate redisTemplate;

    /**
     * 수신자의 안 읽은 메시지 수를 1 증가시킵니다.
     *
     * @param receiverId 안 읽은 메시지를 증가시킬 사용자의 ID
     * @param chatRoomId 채팅방 ID (안 읽은 메시지를 구분하기 위한 키의 일부)
     */
    @Retryable(retryFor = { Exception.class }, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public void increaseUnreadCount(Long receiverId, Long chatRoomId) {
        // Redis에 저장할 키를 생성합니다.
        String key = buildKey(receiverId, chatRoomId);
        // 해당 키의 값을 1 증가시킵니다.
        redisTemplate.opsForValue().increment(key);
    }

    /**
     * 특정 사용자의 특정 채팅방에서의 안 읽은 메시지 수를 조회합니다.
     *
     * @param memberId 사용자의 ID
     * @param chatRoomId 채팅방 ID
     * @return 안 읽은 메시지 수 (값이 없으면 0 반환)
     */
    public int getUnreadCount(Long memberId, Long chatRoomId) {
        // 조회할 Redis 키를 생성합니다.
        String key = buildKey(memberId, chatRoomId);
        // 키에 저장된 값을 문자열로 가져옵니다.
        String value = redisTemplate.opsForValue().get(key);
        // 값이 없으면 0을, 있으면 정수형으로 변환하여 반환합니다.
        return value == null ? 0 : Integer.parseInt(value);
    }

    /**
     * 특정 사용자의 특정 채팅방에서 안 읽은 메시지 수를 초기화(삭제)합니다.
     *
     * @param memberId 사용자의 ID
     * @param chatRoomId 채팅방 ID
     */
    public void resetUnreadCount(Long memberId, Long chatRoomId) {
        // 초기화할 Redis 키를 생성합니다.
        String key = buildKey(memberId, chatRoomId);
        // 해당 키를 Redis에서 삭제하여 안 읽은 메시지 수를 초기화합니다.
        redisTemplate.delete(key);
    }

    /**
     * Redis에 저장할 키를 생성합니다.
     * 키는 "chat:unread{memberId}:{chatRoomId}" 형식입니다.
     *
     * @param memberId 사용자 ID
     * @param chatRoomId 채팅방 ID
     * @return Redis에 저장할 키 문자열
     */
    private String buildKey(Long memberId, Long chatRoomId) {
        return "chat:unread" + memberId + ":" + chatRoomId;
    }
}
