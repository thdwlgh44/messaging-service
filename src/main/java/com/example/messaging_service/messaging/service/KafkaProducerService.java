package com.example.messaging_service.messaging.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/*
* Kafka에 메시지 발행
* */
@Service
public class KafkaProducerService {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    //단일 메시지 전송
    public void sendMessage(String message) {
        kafkaTemplate.send("message-topic", message);
    }

    // Batch 메시지 전송 (한 번에 여러 개)
    public void sendBatchMessages(List<String> messages) {
        messages.forEach(message -> kafkaTemplate.send("message-topic", message));
        System.out.println("📤 Sent batch messages: " + messages.size());
    }
}
