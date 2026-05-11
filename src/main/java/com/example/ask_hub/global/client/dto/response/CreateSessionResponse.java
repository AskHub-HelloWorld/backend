package com.example.ask_hub.global.client.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateSessionResponse {
    @JsonProperty("session_id")
    private String sessionId;
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("team_id")
    private Long teamId;
    private String title;
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
