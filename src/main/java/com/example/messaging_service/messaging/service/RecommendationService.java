package com.example.messaging_service.messaging.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendationService {
    private final RedisService redisService;

    public RecommendationService(RedisService redisService) {
        this.redisService = redisService;
    }

    //최근 본 상품을 기반으로 추천 메시지 생성
    public String generateRecommendation(String userId) {
        List<String> viewedProducts = redisService.getUserViewedProducts(userId);
        if (viewedProducts.isEmpty()) {
            return "👀 새로운 제품을 구경해보세요!";
        }

        String lastViewedProduct = viewedProducts.get(viewedProducts.size() - 1);
        return "🛍️ " + userId + "님! " + lastViewedProduct + "을(를) 보셨네요. 비슷한 상품을 추천해드려요!";
    }

}
