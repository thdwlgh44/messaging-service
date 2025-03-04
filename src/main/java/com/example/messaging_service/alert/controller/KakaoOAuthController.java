package com.example.messaging_service.alert.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class KakaoOAuthController {

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("${kakao.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate;

    public KakaoOAuthController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // ✅ 1. 카카오 로그인 URL 생성 (사용자가 이 URL을 호출하면 카카오 로그인 페이지로 이동)
    @GetMapping("/login/kakao")
    public RedirectView loginWithKakao() {
        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize" +
                "?client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code";
        return new RedirectView(kakaoAuthUrl);
    }

    // ✅ 2. 카카오에서 리다이렉트된 후, 액세스 토큰 요청
    @GetMapping("/callback/kakao")
    public ResponseEntity<String> handleKakaoCallback(@RequestParam("code") String code) {
        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // ✅ MultiValueMap을 사용하여 Form 데이터 전송
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "authorization_code");
        requestBody.add("client_id", clientId);
        requestBody.add("redirect_uri", redirectUri);
        requestBody.add("code", code);
        requestBody.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);

        // 액세스 토큰 요청
        ResponseEntity<Map> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, Map.class);

        // 응답 확인
        if (response.getStatusCode() == HttpStatus.OK) {
            String accessToken = (String) response.getBody().get("access_token");
            return ResponseEntity.ok("✅ 카카오 로그인 성공! 액세스 토큰: " + accessToken);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("❌ 카카오 로그인 실패");
        }
    }

    // ✅ 사용자 정보 가져오는 API
    @GetMapping("/user-info")
    public ResponseEntity<Map> getUserNickname(@RequestHeader("Authorization") String accessToken) {
        // ✅ 액세스 토큰 확인 로그 추가
        System.out.println("🔑 액세스 토큰 확인: " + accessToken);

        // ✅ Bearer 접두사가 포함되지 않은 경우 자동 추가
        if (!accessToken.startsWith("Bearer ")) {
            accessToken = "Bearer " + accessToken;
        }

        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

        // ✅ 요청 헤더 로그 출력
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", accessToken);
        System.out.println("📌 요청 헤더 확인: " + headers);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // ✅ REST 요청 보내기 전에 로그 출력
        System.out.println("📌 요청 URL: " + userInfoUrl);

        ResponseEntity<Map> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, requestEntity, Map.class);

        // ✅ 응답 결과 출력
        System.out.println("📌 응답 상태 코드: " + response.getStatusCode());
        System.out.println("📌 응답 바디: " + response.getBody());

        if (response.getStatusCode() == HttpStatus.OK) {
            return ResponseEntity.ok(response.getBody());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    // ✅ 카카오 로그아웃 API
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String accessToken) {
        String logoutUrl = "https://kapi.kakao.com/v1/user/logout";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", accessToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(logoutUrl, HttpMethod.POST, requestEntity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return ResponseEntity.ok("✅ 로그아웃 성공! (토큰 만료됨)");
        } else {
            return ResponseEntity.status(response.getStatusCode()).body("❌ 로그아웃 실패");
        }
    }

}
