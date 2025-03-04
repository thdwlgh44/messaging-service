package com.example.messaging_service.messaging.controller;

import com.example.messaging_service.messaging.model.UserEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events")
public class MarketingEventController {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public MarketingEventController(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    //사용자 이벤트 수집
    @PostMapping("/track")
    public ResponseEntity<String> trackEvent(@RequestBody UserEvent event) throws Exception {
        String eventJson = objectMapper.writeValueAsString(event); // ✅ Jackson으로 변환, LocalDateTime 직렬화 문제 해결
        kafkaTemplate.send("user-events", eventJson);
        return ResponseEntity.ok("✅ 사용자 이벤트 저장: " + event.getEventType());
    }
}
