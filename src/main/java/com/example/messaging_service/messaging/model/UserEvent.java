package com.example.messaging_service.messaging.model;

import lombok.Getter;

import java.time.LocalDateTime;

/*
* 사용자 이벤트 데이터 모델
* */
@Getter
public class UserEvent {
    private String userId;
    private String eventType;  // "PRODUCT_VIEW"
    private String productId;
    private LocalDateTime timestamp;

    public UserEvent(String userId, String eventType, String productId, LocalDateTime timestamp) {
        this.userId = userId;
        this.eventType = eventType;
        this.productId = productId;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "UserEvent{" +
                "userId='" + userId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", productId='" + productId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
