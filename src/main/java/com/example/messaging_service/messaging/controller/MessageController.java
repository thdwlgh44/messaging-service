package com.example.messaging_service.messaging.controller;

import com.example.messaging_service.messaging.model.MessageRequest;
import com.example.messaging_service.messaging.service.ActiveMQProducerService;
import com.example.messaging_service.messaging.service.KafkaProducerService;
import com.example.messaging_service.messaging.service.RedisService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final RedisService redisService;
    private final KafkaProducerService kafkaProducerService;
    private final ActiveMQProducerService activeMQProducerService;

    public MessageController(RedisService redisService, KafkaProducerService kafkaProducerService, ActiveMQProducerService activeMQProducerService) {
        this.redisService = redisService;
        this.kafkaProducerService = kafkaProducerService;
        this.activeMQProducerService = activeMQProducerService;
    }

    // Kafka를 통해 비동기 메시지 전송 (대량 트래픽 처리)
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
}
