package com.example.ask_hub.team.domain.dto.response;

import com.example.ask_hub.team.domain.enums.Role;

public record MessageGetResponse(
        MessageResponse response
) {
    public static MessageGetResponse from(
            Long messageId,
            String content,
            Role role
    ){
        return new MessageGetResponse(new MessageResponse(
                messageId,
                content,
                role
        ));
    }

    record MessageResponse(
            Long messageId,
            String content,
            Role role
    ){
    }
}
