package com.example.messaging_service.messaging.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import java.util.HashMap;
import java.util.Map;

@Configuration //Kafka Consumer 관련 Bean 등록
public class KafkaConfig {

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); //로컬 개발 환경 서버 주소
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "messaging-group"); //같은 groupId를 사용하는 Consumer들은 하나의 메시지를 나눠서 처리
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); //바이너리 형태 kafka 메시지를 String 변환
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);  // 수동 커밋해야 함. 메시지를 처리한 후 커밋해야 장애 발생 시 중복 처리 방지 가능.
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 10);  // Batch 크기 설정, 한 번에 가져올 최대 메시지 개수 10개로 설정으로 성능 최적화.
        return new DefaultKafkaConsumerFactory<>(props);
    }

    //@kafkaListener가 Batch모드로 동작하도록 설정하는 부분
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> batchFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>(); //메시지를 병렬로 소비할 수 있는 컨테이너 팩토리 생성.
        factory.setConsumerFactory(consumerFactory());
        factory.setBatchListener(true);  // Batch 모드 활성화
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL); // 수동 커밋
        return factory;
    }
}
