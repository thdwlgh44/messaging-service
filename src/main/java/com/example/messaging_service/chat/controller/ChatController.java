package com.example.messaging_service.chat.controller;

import com.example.messaging_service.chat.dto.ChatMessage;
import com.example.messaging_service.chat.repository.ChatMessageRepository;
import com.example.messaging_service.messaging.service.KafkaProducerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/*
* 채팅 컨트롤러
* */
//@RestController: Websocket 메시지를 받을 수 없음
@Controller
//@RequestMapping("/chat")
public class ChatController {
    private final KafkaProducerService kafkaProducerService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;
    private final ChatMessageRepository chatMessageRepository;

    public ChatController(KafkaProducerService kafkaProducerService, SimpMessagingTemplate messagingTemplate, ObjectMapper objectMapper, ChatMessageRepository chatMessageRepository) {
        this.kafkaProducerService = kafkaProducerService;
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = objectMapper;
        this.chatMessageRepository = chatMessageRepository;
    }

    @MessageMapping("/send")
    public void sendMessage(@Payload ChatMessage message) {
        try {
            System.out.println("📥 받은 메시지: " + message.getContent());

            // Kafka에 메시지를 JSON으로 저장
            String jsonMessage = objectMapper.writeValueAsString(message);
            kafkaProducerService.sendMessage(jsonMessage);

            //db에 메시지 저장
            chatMessageRepository.save(message);
            System.out.println("✅ 메시지가 DB에 저장됨: " + message.getContent());

            // WebSocket을 통해 특정 채팅방으로 메시지 전송
            messagingTemplate.convertAndSend("/topic/chatroom-" + message.getChatRoomId(), message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ✅ 특정 채팅방의 메시지 조회 API
    @GetMapping("/messages/{roomId}")
    public List<ChatMessage> getChatMessages(@PathVariable String roomId) {
        return chatMessageRepository.findByChatRoomId(roomId);
    }
}

