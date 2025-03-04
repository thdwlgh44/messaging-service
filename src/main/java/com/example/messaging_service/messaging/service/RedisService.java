package com.example.messaging_service.messaging.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/*
* 메시지를 redis에 저장하고 가져오는 기능 + 사용자 이벤트 저장 서비스
* */
@Service
public class RedisService {
    private final StringRedisTemplate redisTemplate;
    private static final int MAX_RECENT_PRODUCTS = 5; // 최근 본 상품 최대 개수
    private static final long TTL_DAYS = 7; // 7일 후 자동 삭제

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

    // ✅ 사용자별 최근 본 상품 저장 (최대 5개, 7일 유지)
    public void saveUserViewedProduct(String userId, String productId) {
        String key = "user:recent_products:" + userId;

        // 상품 추가 (중복 제거 후 저장)
        redisTemplate.opsForList().remove(key, 0, productId);
        redisTemplate.opsForList().rightPush(key, productId);

        // 리스트 길이가 5개 초과하면 가장 오래된 데이터 삭제
        if (Boolean.TRUE.equals(redisTemplate.opsForList().size(key) > MAX_RECENT_PRODUCTS)) {
            redisTemplate.opsForList().leftPop(key);
        }

        // TTL 설정 (7일 후 자동 삭제)
        redisTemplate.expire(key, TTL_DAYS, TimeUnit.DAYS);
    }

    // ✅ 사용자별 최근 본 상품 목록 가져오기
    public List<String> getUserViewedProducts(String userId) {
        String key = "user:recent_products:" + userId;
        return redisTemplate.opsForList().range(key, 0, MAX_RECENT_PRODUCTS - 1);
    }
}