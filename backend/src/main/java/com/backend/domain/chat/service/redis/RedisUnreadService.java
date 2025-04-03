package com.backend.domain.chat.service.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisUnreadService {

    private final StringRedisTemplate redisTemplate;

    public void increaseUnreadCount(Long receiverId, Long chatRoomId) {
        String key = buildKey(receiverId, chatRoomId);
        redisTemplate.opsForValue().increment(key);
    }

    public int getUnreadCount(Long memberId, Long chatRoomId) {
        String key = buildKey(memberId, chatRoomId);
        String value = redisTemplate.opsForValue().get(key);
        return value == null ? 0 : Integer.parseInt(value);
    }

    public void resetUnreadCount(Long memberId, Long chatRoomId) {
        String key = buildKey(memberId, chatRoomId);
        redisTemplate.delete(key);
    }

    private String buildKey(Long memberId, Long chatRoomId) {
        return "chat:unread" + memberId + ":" + chatRoomId;
    }
}
