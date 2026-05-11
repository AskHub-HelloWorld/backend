package com.example.ask_hub.team.domain.dto.response;

public record MessageGetResponse(
        Long messageId,
        String content
) {
    public static MessageGetResponse from(
            Long messageId,
            String content
    ){
        return new MessageGetResponse(
                messageId,
                content
        );
    }
}
