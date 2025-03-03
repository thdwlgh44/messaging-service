package com.example.messaging_service.messaging.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/*
* 메시지를 redis에 저장하고 가져오는 기능
* */
@Service
public class RedisService {
    private final StringRedisTemplate redisTemplate;

    public RedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 메시지 저장 + TTL 적용 (5분 후 자동 삭제)
    public void saveMessage(String key, String value, long ttlMinutes) {
        redisTemplate.opsForValue().set(key, value, ttlMinutes, TimeUnit.MINUTES);
        System.out.println("📌 Redis 저장: " + key + " = " + value + " (TTL: " + ttlMinutes + "분)");
    }

    // 메시지 조회
    public String getMessage(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // 메시지 삭제
    public void deleteMessage(String key) {
        redisTemplate.delete(key);
    }

    // 특정 키의 TTL 확인
    public Long getTTL(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }
}