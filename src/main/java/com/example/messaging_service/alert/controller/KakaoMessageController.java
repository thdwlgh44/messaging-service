package com.example.messaging_service.alert.controller;

import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/message")
public class KakaoMessageController {

    private final RestTemplate restTemplate;

    public KakaoMessageController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // 카카오톡 메시지 보내기 (`talk_message` 사용)
    @PostMapping("/send")
    public ResponseEntity<String> sendKakaoMessage(
            @RequestHeader("Authorization") String accessToken,
            @RequestParam("message") String message) {

        String url = "https://kapi.kakao.com/v2/api/talk/memo/default/send";

        // ✅ 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", accessToken); // Bearer 포함된 올바른 액세스 토큰 전달
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        System.out.println("📌 요청 헤더 확인: " + headers);

        // ✅ 요청 바디 설정
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("template_object", "{ \"object_type\": \"text\", \"text\": \"" + message + "\", \"link\": { \"web_url\": \"https://developers.kakao.com\" }}");

        HttpEntity<MultiValueMap<String, String>> httpRequest = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, httpRequest, Map.class);

        // ✅ 응답 로그 출력
        System.out.println("📌 응답 상태 코드: " + response.getStatusCode());
        System.out.println("📌 응답 바디: " + response.getBody());

        if (response.getStatusCode() == HttpStatus.OK) {
            return ResponseEntity.ok("✅ 메시지 전송 성공!");
        } else {
            return ResponseEntity.status(response.getStatusCode()).body("❌ 메시지 전송 실패");
        }
    }




}

