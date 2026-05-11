package com.example.ask_hub.team.domain.dto.request;

import jakarta.validation.constraints.NotNull;

public record MessageCreateRequest(
        @NotNull
        Long sessionId,
        @NotNull
        Long teamId,
        @NotNull
        String message
) {
}
