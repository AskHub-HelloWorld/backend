package com.example.ask_hub.team.domain.dto.response;

public record MessageCreateResponse(
        Long requestMessageId,
        Long responseMessageId
) {
    public static MessageCreateResponse from(Long requestMessageId, Long responseMessageId) {
        return new MessageCreateResponse(requestMessageId, responseMessageId);
    }
}
