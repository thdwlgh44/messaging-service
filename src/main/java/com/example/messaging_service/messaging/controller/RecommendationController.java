package com.example.messaging_service.messaging.controller;

import com.example.messaging_service.messaging.service.RecommendationService;
import com.example.messaging_service.messaging.service.RedisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recommendations")
public class RecommendationController {
    private final RedisService redisService;
    private final RecommendationService recommendationService;

    public RecommendationController(RedisService redisService, RecommendationService recommendationService) {
        this.redisService = redisService;
        this.recommendationService = recommendationService;
    }

    //특정 사용자의 최근 본 상품 목록 조회
    @GetMapping("/recent/{userId}")
    public ResponseEntity<List<String>> getRecentViewedProducts(@PathVariable String userId) {
        List<String> recentProducts = redisService.getUserViewedProducts(userId);
        return ResponseEntity.ok(recentProducts);
    }

    // ✅ 특정 사용자의 최근 본 상품 기반 추천 메시지 제공
    @GetMapping("/{userId}")
    public ResponseEntity<String> getRecommendation(@PathVariable String userId) {
        String recommendation = recommendationService.generateRecommendation(userId);
        return ResponseEntity.ok(recommendation);
    }

}

