package com.example.messaging_service.messaging.service;

import com.example.messaging_service.chat.dto.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/*
* Kafka에 메시지 발행
* */
@Service
public class KafkaProducerService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    //단일 메시지 전송
    public void sendMessage(String message) {
        kafkaTemplate.send("message-topic", message);
        System.out.println("📤 Kafka 메시지 전송: " + message);
    }

    // Batch 메시지 전송 (한 번에 여러 개)
    public void sendChatMessage(ChatMessage chatMessage) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(chatMessage);
            kafkaTemplate.send("chatroom-" + chatMessage.getChatRoomId(), jsonMessage);
            System.out.println("📤 Kafka 채팅 메시지 전송: " + jsonMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
