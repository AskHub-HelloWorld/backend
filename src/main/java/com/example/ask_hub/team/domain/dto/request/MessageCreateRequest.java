package com.example.ask_hub.team.domain.dto.request;


public record MessageCreateRequest(
        Long sessionId,
        Long teamId,
        String message
) {
}
