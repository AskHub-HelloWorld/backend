package com.example.ask_hub.global.client;

import com.example.ask_hub.global.client.dto.response.CreateSessionResponse;
import com.example.ask_hub.global.client.dto.response.SendMessageResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AiServerClient {

    private final RestClient restClient;
    private final AiServerAuthHeader authHeader;
    private final ObjectMapper objectMapper;  // 추가

    @Value("${ai.server.url}")
    private String aiServerUrl;

    public CreateSessionResponse createSession(Long userId, Long teamId, String title) {
        String path = "/v1/chat/sessions";
        Map<String, String> headers = authHeader.generate("POST", path, userId, teamId);

        try {
            URL url = new URL(aiServerUrl + path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            headers.forEach(conn::setRequestProperty);

            String body = "{\"title\": \"" + title + "\"}";
            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }

            try (InputStream is = conn.getInputStream()) {
                String response = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                return objectMapper.readValue(response, CreateSessionResponse.class);  // 변경
            }
        } catch (Exception e) {
            log.error("AI 서버 호출 실패: {}", e.getMessage(), e);
            throw new RuntimeException("AI 서버 호출 실패: " + e.getMessage(), e);
        }
    }

    public SendMessageResponse sendMessage(Long userId, Long teamId, String sessionId, String message) {
        String path = "/v1/chat/sessions/" + sessionId + "/messages";
        Map<String, String> headers = authHeader.generate("POST", path, userId, teamId);

        try {
            URL url = new URL(aiServerUrl + path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            headers.forEach(conn::setRequestProperty);

            String body = "{\"message\": \"" + message + "\"}";
            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }

            try (InputStream is = conn.getResponseCode() >= 400
                    ? conn.getErrorStream()
                    : conn.getInputStream()) {
                String response = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                log.info("AI 서버 응답: {}", response);  // 응답 내용 확인
                return objectMapper.readValue(response, SendMessageResponse.class);
            }

        } catch (Exception e) {
            log.error("AI 서버 호출 실패: {}", e.getMessage(), e);
            throw new RuntimeException("AI 서버 호출 실패: " + e.getMessage(), e);
        }
    }


    // 파일 업로드
    public void uploadFile(Long userId, Long teamId, String sessionId, MultipartFile file) {
        String path = "/v1/files/upload";
        Map<String, String> headers = authHeader.generate("POST", path, userId, teamId);

        try {
            String boundary = "----Boundary" + System.currentTimeMillis();
            URL url = new URL(aiServerUrl + path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            headers.forEach(conn::setRequestProperty);

            try (OutputStream os = conn.getOutputStream()) {
                // file 파트
                os.write(("--" + boundary + "\r\n").getBytes());
                os.write(("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getOriginalFilename() + "\"\r\n").getBytes());
                os.write(("Content-Type: " + file.getContentType() + "\r\n\r\n").getBytes());
                os.write(file.getBytes());
                os.write("\r\n".getBytes());

                // purpose 파트
                os.write(("--" + boundary + "\r\n").getBytes());
                os.write("Content-Disposition: form-data; name=\"purpose\"\r\n\r\n".getBytes());
                os.write("convention\r\n".getBytes());

                // session_id 파트
                os.write(("--" + boundary + "\r\n").getBytes());
                os.write("Content-Disposition: form-data; name=\"session_id\"\r\n\r\n".getBytes());
                os.write((sessionId + "\r\n").getBytes());

                os.write(("--" + boundary + "--\r\n").getBytes());
            }

            int responseCode = conn.getResponseCode();
            log.info("파일 업로드 응답 코드: {}", responseCode);

        } catch (Exception e) {
            log.error("파일 업로드 실패: {}", e.getMessage(), e);
            throw new RuntimeException("파일 업로드 실패: " + e.getMessage(), e);
        }
    }
}