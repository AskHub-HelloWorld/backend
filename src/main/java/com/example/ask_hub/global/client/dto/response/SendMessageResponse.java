package com.example.ask_hub.global.client.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SendMessageResponse {
    @JsonProperty("session_id")
    private String sessionId;
    @JsonProperty("user_message_id")
    private String userMessageId;
    @JsonProperty("assistant_message_id")
    private String assistantMessageId;
    private String answer;
    private Boolean answerable;
    private List<Object> citations;
}
