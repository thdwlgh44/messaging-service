package com.example.messaging_service.chat.repository;

import com.example.messaging_service.chat.dto.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomId(String chatRoomId); //특정 채팅방 메시지 조회
}
