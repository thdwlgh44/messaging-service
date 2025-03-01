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

    public void saveMessage(String key, String value, long expiration) {
        redisTemplate.opsForValue().set(key, value, expiration, TimeUnit.MINUTES);
    }

    public String getMessage(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteMessage(String key) {
        redisTemplate.delete(key);
    }
}