package com.example.messaging_service.messaging.repository;

import com.example.messaging_service.messaging.entity.MessageLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageLogRepository extends JpaRepository<MessageLog, Long> {
}
