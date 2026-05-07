package com.example.ask_hub.global.client;

import com.example.ask_hub.global.client.dto.request.CreateSessionRequest;
import com.example.ask_hub.global.client.dto.request.SendMessageRequest;
import com.example.ask_hub.global.client.dto.response.CreateSessionResponse;
import com.example.ask_hub.global.client.dto.response.SendMessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AiServerClient {

    private final RestClient restClient;
    private final AiServerAuthHeader authHeader;

    @Value("${ai.server.url}")
    private String aiServerUrl;

    // 채팅 세션 생성
    public CreateSessionResponse createSession(Long userId, Long teamId, String title) {
        String path = "/v1/chat/sessions";
        Map<String, String> headers = authHeader.generate("POST", path, userId, teamId);

        return restClient.post()
                .uri(aiServerUrl + path)
                .headers(h -> headers.forEach(h::add))
                .body(new CreateSessionRequest(title))
                .retrieve()
                .body(CreateSessionResponse.class);
    }

    // 메시지 전송 (동기)
    public SendMessageResponse sendMessage(Long userId, Long teamId, String sessionId, String message) {
        String path = "/v1/chat/sessions/" + sessionId + "/messages";
        Map<String, String> headers = authHeader.generate("POST", path, userId, teamId);

        return restClient.post()
                .uri(aiServerUrl + path)
                .headers(h -> headers.forEach(h::add))
                .body(new SendMessageRequest(message))
                .retrieve()
                .body(SendMessageResponse.class);
    }

    // 파일 업로드
    public void uploadFile(Long userId, Long teamId, String sessionId, MultipartFile file) {
        String path = "/v1/files/upload";
        Map<String, String> headers = authHeader.generate("POST", path, userId, teamId);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource());
        body.add("purpose", "chat_attachment");
        body.add("session_id", sessionId);

        restClient.post()
                .uri(aiServerUrl + path)
                .headers(h -> headers.forEach(h::add))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }
}