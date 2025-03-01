package com.example.messaging_service.messaging.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageRequest {
    private String type;  // "SMS", "EMAIL", "PUSH", "KAKAO"
    private String recipient;
    private String content;
}