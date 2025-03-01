package com.example.messaging_service.messaging.service;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

/*
 * ActiveMQ 메시지 발행
 * */
@Service
public class ActiveMQProducerService {
    private final JmsTemplate jmsTemplate;

    public ActiveMQProducerService(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendMessage(String message) {
        jmsTemplate.convertAndSend("message-queue", message);
    }
}
