package com.example.ask_hub.global.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AiServerAuthHeader {

    @Value("${ai.server.secret}")
    private String secret;

    public Map<String, String> generate(String method, String path, Long userId, Long teamId) {
        String timestamp = String.valueOf(Instant.now().getEpochSecond());
        String teamIdStr = teamId == null ? "" : String.valueOf(teamId);
        String query = "";

        String payload = String.join("\n",
                timestamp,
                method.toUpperCase(),
                path,
                query,
                String.valueOf(userId),
                teamIdStr
        );

        log.info("=== HMAC 서명 디버깅 ===");
        log.info("timestamp: {}", timestamp);
        log.info("method: {}", method.toUpperCase());
        log.info("path: {}", path);
        log.info("query: {}", query);
        log.info("userId: {}", userId);
        log.info("teamId: {}", teamIdStr);
        log.info("payload: {}", payload);
        log.info("secret: {}", secret);

        String signature = generateHmac(payload);
        log.info("signature: {}", signature);

        Map<String, String> headers = new HashMap<>();
        headers.put("x-askhub-user-id", String.valueOf(userId));
        headers.put("x-askhub-team-id", teamIdStr);
        headers.put("x-askhub-timestamp", timestamp);
        headers.put("x-askhub-signature", signature);
        return headers;
    }

    private String generateHmac(String payload) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(
                    secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("HMAC 서명 생성 실패", e);
        }
    }
}
