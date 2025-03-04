package com.example.messaging_service.messaging.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    // ✅ RestTemplate Bean 등록
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
