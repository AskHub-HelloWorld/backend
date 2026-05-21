package com.example.ask_hub.global.client;

import com.example.ask_hub.global.client.dto.response.CreateSessionResponse;
import com.example.ask_hub.global.client.dto.response.SendMessageResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AiServerClient {

    private final AiServerAuthHeader authHeader;
    private final ObjectMapper objectMapper;

    @Value("${ai.server.url}")
    private String aiServerUrl;

    private HttpURLConnection openConnection(String method, String path, Map<String, String> headers) throws Exception {
        URL url = new URL(aiServerUrl + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");
        headers.forEach(conn::setRequestProperty);
        return conn;
    }

    private String readResponse(HttpURLConnection conn) throws Exception {
        InputStream is = conn.getResponseCode() >= 400
                ? conn.getErrorStream()
                : conn.getInputStream();
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }

    public CreateSessionResponse createSession(Long userId, Long teamId, String title) {
        String path = "/v1/chat/sessions";
        Map<String, String> headers = authHeader.generate("POST", path, userId, teamId);

        try {
            HttpURLConnection conn = openConnection("POST", path, headers);
            String body = objectMapper.writeValueAsString(Map.of("title", title));

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }

            return objectMapper.readValue(readResponse(conn), CreateSessionResponse.class);

        } catch (Exception e) {
            log.error("세션 생성 실패: {}", e.getMessage(), e);
            throw new RuntimeException("세션 생성 실패: " + e.getMessage(), e);
        }
    }

    public SendMessageResponse sendMessage(
            Long userId,
            Long teamId,
            String sessionId,
            String message,
            List<String> fileIds
    ) {
        String path = "/v1/chat/sessions/" + sessionId + "/messages";
        Map<String, String> headers = authHeader.generate("POST", path, userId, teamId);

        try {
            HttpURLConnection conn = openConnection("POST", path, headers);

            if ((message == null || message.isBlank())
                    && fileIds != null
                    && !fileIds.isEmpty()) {

                message = "첨부한 파일을 분석해주세요.";
            }

            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("message", message);

            if (fileIds != null && !fileIds.isEmpty()) {
                bodyMap.put("file_ids", fileIds);
            }

            String body = objectMapper.writeValueAsString(bodyMap);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }

            return objectMapper.readValue(
                    readResponse(conn),
                    SendMessageResponse.class
            );

        } catch (Exception e) {
            log.error("메시지 전송 실패: {}", e.getMessage(), e);
            throw new RuntimeException("메시지 전송 실패: " + e.getMessage(), e);
        }
    }

    public String uploadFile(Long userId, Long teamId, String sessionId, MultipartFile file, String purpose) {
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
                os.write(("--" + boundary + "\r\n").getBytes());
                os.write(("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getOriginalFilename() + "\"\r\n").getBytes());
                os.write(("Content-Type: " + file.getContentType() + "\r\n\r\n").getBytes());
                os.write(file.getBytes());
                os.write("\r\n".getBytes());

                os.write(("--" + boundary + "\r\n").getBytes());
                os.write("Content-Disposition: form-data; name=\"purpose\"\r\n\r\n".getBytes());
                os.write((purpose + "\r\n").getBytes());

                if (sessionId != null) {
                    os.write(("--" + boundary + "\r\n").getBytes());
                    os.write("Content-Disposition: form-data; name=\"session_id\"\r\n\r\n".getBytes());
                    os.write((sessionId + "\r\n").getBytes());
                }

                os.write(("--" + boundary + "--\r\n").getBytes());
            }

            String response = readResponse(conn);
            JsonNode root = objectMapper.readTree(response);
            return root.get("id").asText();

        } catch (Exception e) {
            log.error("파일 업로드 실패: {}", e.getMessage(), e);
            throw new RuntimeException("파일 업로드 실패: " + e.getMessage(), e);
        }
    }



    public String registerSource(Long userId, Long teamId, String name, String repoUrl, String defaultBranch) {
        String path = "/v1/sources";
        Map<String, String> headers = authHeader.generate("POST", path, userId, teamId);

        try {
            HttpURLConnection conn = openConnection("POST", path, headers);

            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("source_type", "repository");
            bodyMap.put("name", name);
            bodyMap.put("repo_url", repoUrl);
            bodyMap.put("default_branch", defaultBranch);

            String body = objectMapper.writeValueAsString(bodyMap);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }

            String response = readResponse(conn);
            return objectMapper.readTree(response).get("source_id").asText();

        } catch (Exception e) {
            log.error("RAG 소스 등록 실패: {}", e.getMessage(), e);
            throw new RuntimeException("RAG 소스 등록 실패: " + e.getMessage(), e);
        }
    }

    public void deleteSource(Long userId, Long teamId, String sourceId) {
        String path = "/v1/sources/" + sourceId;
        Map<String, String> headers = authHeader.generate("DELETE", path, userId, teamId);

        try {
            URL url = new URL(aiServerUrl + path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Content-Type", "application/json");
            headers.forEach(conn::setRequestProperty);

            int responseCode = conn.getResponseCode();

            if (responseCode >= 400) {
                InputStream errorStream = conn.getErrorStream();
                String errorMessage = errorStream != null
                        ? new String(errorStream.readAllBytes(), StandardCharsets.UTF_8)
                        : "Unknown error";
                log.error("RAG 소스 삭제 실패: {}", errorMessage);
                throw new RuntimeException("RAG 소스 삭제 실패: " + errorMessage);
            }

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("RAG 소스 삭제 실패: {}", e.getMessage(), e);
            throw new RuntimeException("RAG 소스 삭제 실패: " + e.getMessage(), e);
        }
    }

    public String registerSource(
            Long userId,
            Long teamId,
            String fileId,
            String name
    ) {
        String path = "/v1/sources";
        Map<String, String> headers =
                authHeader.generate("POST", path, userId, teamId);

        try {
            HttpURLConnection conn =
                    openConnection("POST", path, headers);

            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("source_type", "document");
            bodyMap.put("name", name);
            bodyMap.put("file_id", fileId);

            String body = objectMapper.writeValueAsString(bodyMap);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }

            String response = readResponse(conn);


            return objectMapper.readTree(response)
                    .get("source_id")
                    .asText();

        } catch (Exception e) {
            log.error("RAG 소스 등록 실패: {}", e.getMessage(), e);
            throw new RuntimeException(
                    "RAG 소스 등록 실패: " + e.getMessage(),
                    e
            );
        }
    }
}