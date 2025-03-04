package com.example.messaging_service.messaging.controller;

import com.example.messaging_service.alert.service.KakaoAlertService;
import com.example.messaging_service.messaging.model.MessageRequest;
import com.example.messaging_service.messaging.service.ActiveMQProducerService;
import com.example.messaging_service.messaging.service.KafkaProducerService;
import com.example.messaging_service.messaging.service.RecommendationService;
import com.example.messaging_service.messaging.service.RedisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final RedisService redisService;
    private final KafkaProducerService kafkaProducerService;
    private final ActiveMQProducerService activeMQProducerService;
    private final RecommendationService recommendationService;
    private final KakaoAlertService kakaoAlertService;

    public MessageController(RedisService redisService, KafkaProducerService kafkaProducerService, ActiveMQProducerService activeMQProducerService, RecommendationService recommendationService, KakaoAlertService kakaoAlertService) {
        this.redisService = redisService;
        this.kafkaProducerService = kafkaProducerService;
        this.activeMQProducerService = activeMQProducerService;
        this.recommendationService = recommendationService;
        this.kakaoAlertService = kakaoAlertService;
    }

    // ✅ Kafka로 단일 메시지 전송 (중복 방지 + 캐싱 적용)
    @PostMapping("/send")
    public String sendMessageToKafka(@RequestBody MessageRequest request) {
        String cacheKey = request.getType() + ":" + request.getRecipient();

        // 중복 메시지 확인
        if (redisService.getMessage(cacheKey) != null) {
            return "❌ 메시지가 이미 전송되었습니다.";
        }

        // 메시지를 5분 동안 Redis에 캐싱 (중복 방지)
        redisService.saveMessage(cacheKey, request.getContent(), 5);

        // Kafka로 메시지 전송
        String message = request.getType() + ":" + request.getRecipient() + ":" + request.getContent();
        kafkaProducerService.sendMessage(message);

        return "✅ 메시지가 Kafka에 전송되었습니다.";
    }

    // ✅ Kafka로 Batch 메시지 전송
    @PostMapping("/send-batch")
    public String sendBatchMessagesToKafka(@RequestBody List<MessageRequest> requests) {
        // 중복되지 않은 메시지 리스트 생성
        List<String> validMessages = requests.stream()
                .filter(request -> redisService.getMessage(request.getType() + ":" + request.getRecipient()) == null)
                .map(request -> {
                    String cacheKey = request.getType() + ":" + request.getRecipient();
                    redisService.saveMessage(cacheKey, request.getContent(), 5); // 중복 방지 캐싱
                    return request.getType() + ":" + request.getRecipient() + ":" + request.getContent();
                })
                .collect(Collectors.toList());

        if (validMessages.isEmpty()) {
            return "❌ 중복된 메시지로 인해 전송할 메시지가 없습니다.";
        }

        // Kafka로 Batch 메시지 전송
        kafkaProducerService.sendBatchMessages(validMessages);

        return "✅ Batch 메시지가 Kafka에 전송되었습니다.";
    }

    // ActiveMQ를 통해 실시간 메시지 전송 (빠른 응답 필요)
    @PostMapping("/send-realtime")
    public String sendMessageToActiveMQ(@RequestBody MessageRequest request) {
        String cacheKey = request.getType() + ":" + request.getRecipient();

        // 중복 메시지 확인
        if (redisService.getMessage(cacheKey) != null) {
            return "❌ 메시지가 이미 전송되었습니다.";
        }

        // 메시지를 5분 동안 Redis에 캐싱 (중복 방지)
        redisService.saveMessage(cacheKey, request.getContent(), 5);

        // ActiveMQ로 메시지 전송
        String message = request.getType() + ":" + request.getRecipient() + ":" + request.getContent();
        activeMQProducerService.sendMessage(message);

        return "✅ 메시지가 ActiveMQ에 전송되었습니다.";
    }

    // ✅ AI 추천 메시지를 카카오 알림톡으로 전송
    @PostMapping("/send/kakao/{userId}")
    public ResponseEntity<String> sendKakaoMessage(@PathVariable String userId) {
        String recommendation = recommendationService.generateRecommendation(userId);
        kakaoAlertService.sendKakaoAlert(userId, recommendation);
        return ResponseEntity.ok("✅ 카카오 알림톡 전송 완료: " + recommendation);
    }
}
