package com.example.messaging_service.messaging.service;

import com.example.messaging_service.messaging.entity.MessageLog;
import com.example.messaging_service.messaging.repository.MessageLogRepository;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
public class ActiveMQConsumerService {

    private final MessageLogRepository messageLogRepository;

    public ActiveMQConsumerService(MessageLogRepository messageLogRepository) {
        this.messageLogRepository = messageLogRepository;
    }

    @JmsListener(destination = "message-queue")
    public void consumeMessage(String message) {
        System.out.println("📥 ActiveMQ received: " + message);

        String[] parts = message.split(":");
        MessageLog log = new MessageLog();
        log.setType(parts[0]);
        log.setRecipient(parts[1]);
        log.setContent(parts[2]);

        messageLogRepository.save(log);
    }
}
