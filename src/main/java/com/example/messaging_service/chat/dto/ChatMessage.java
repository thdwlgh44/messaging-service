package com.example.messaging_service.chat.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ChatMessage {
    public enum MessageType {
        CHAT, JOIN, LEAVE, FILE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //자동증가 id로 db에서 메시지 관리, 파일전송기능 고려 확장 설계

    private String chatRoomId;
    private MessageType type;
    private String sender;
    private String recipient;
    private String content;
    private LocalDateTime timestamp = LocalDateTime.now();
}

