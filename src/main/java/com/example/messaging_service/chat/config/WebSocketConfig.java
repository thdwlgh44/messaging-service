package com.example.messaging_service.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/*
* websocket & stomp 기반 채팅 서버
* */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");  // 클라이언트가 구독할 경로
        registry.setApplicationDestinationPrefixes("/app"); // 메시지 전송 경로
        registry.setUserDestinationPrefix("/user"); // 1:1 메시징 지원
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat") //react에서 접속할 WebSocket URL
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}


