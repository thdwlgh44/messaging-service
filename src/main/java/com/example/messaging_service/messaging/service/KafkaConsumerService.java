package com.example.messaging_service.messaging.service;

import com.example.messaging_service.messaging.entity.MessageLog;
import com.example.messaging_service.messaging.repository.MessageLogRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/*
* Kafka 메시지 소비 및 DB 저장
* */
@Service
public class KafkaConsumerService {

    private final MessageLogRepository messageLogRepository;

    public KafkaConsumerService(MessageLogRepository messageLogRepository) {
        this.messageLogRepository = messageLogRepository;
    }

    @KafkaListener(topics = "message-topic", groupId = "messaging-group")
    public void consumeMessage(String message) {
        System.out.println("📥 Kafka received: " + message);

        String[] parts = message.split(":");
        MessageLog log = new MessageLog();
        log.setType(parts[0]);
        log.setRecipient(parts[1]);
        log.setContent(parts[2]);

        messageLogRepository.save(log);
    }
}
