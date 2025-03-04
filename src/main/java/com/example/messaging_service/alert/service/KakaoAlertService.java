package com.example.messaging_service.alert.service;

import org.springframework.stereotype.Service;

@Service
public class KakaoAlertService {

    // ✅ 카카오 알림톡 전송 (Mock)
    public void sendKakaoAlert(String userId, String message) {
        System.out.println("📩 [MOCK] 카카오 알림톡 전송: " + userId + " → " + message);
        // TODO: 실제 카카오 알림톡 API 연동 필요
    }
}
