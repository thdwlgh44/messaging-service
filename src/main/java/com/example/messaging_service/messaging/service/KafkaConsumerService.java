package com.example.messaging_service.messaging.service;

import com.example.messaging_service.messaging.entity.MessageLog;
import com.example.messaging_service.messaging.repository.MessageLogRepository;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/*
* Kafka 메시지 소비 및 DB 저장 (Batch 처리 + 병렬 처리 적용)
* */
@Service
public class KafkaConsumerService {

    private final MessageLogRepository messageLogRepository;
    private final Counter messageCounter;

    public KafkaConsumerService(MessageLogRepository messageLogRepository, MeterRegistry meterRegistry) {
        this.messageLogRepository = messageLogRepository;
        this.messageCounter = meterRegistry.counter("kafka.consumer.processed.messages"); // Kafka 메시지 처리량 카운터
    }

    //개별 메시지를 받도록 되어있으므로 Batch 처리를 지원하는 전용 팩토리 필요 (KafkaConfig.java)
    //여러 개의 메시지 예를 들어 1초에 10,000개의 메시지가 들어오더라도 싱글 스레드인 kafka의 처리 지연을 막고 병렬 소비 활성화. 여러 메시지 동시 처리 가능.
    @KafkaListener(topics = "message-topic", groupId = "messaging-group", containerFactory = "batchFactory")
    @Timed(value = "kafka.message.process.time", description = "Time taken to process messages")
    public void consumeMessages(List<ConsumerRecord<String, String>> records, Acknowledgment ack) {
        long startTime = System.currentTimeMillis(); // 처리 시간 측정 시작

        System.out.println("📥 Batch Received: " + records.size() + " messages");

        List<MessageLog> logs = records.stream().map(record -> {
            String[] parts = record.value().split(":");
            MessageLog log = new MessageLog();
            log.setType(parts[0]);
            log.setRecipient(parts[1]);
            log.setContent(parts[2]);
            return log;
        }).collect(Collectors.toList());

        // 한 번에 여러 개의 메시지를 받아 Bulk Insert
        messageLogRepository.saveAll(logs);

        // Kafka 메시지 처리량 카운터 증가
        messageCounter.increment(records.size());

        long endTime = System.currentTimeMillis(); // 처리 시간 측정 종료
        System.out.println("✅ Batch 처리 시간: " + (endTime - startTime) + " ms");

        // 수동 커밋 (오프셋 관리) - 중복 처리 방지
        ack.acknowledge();
    }
}
