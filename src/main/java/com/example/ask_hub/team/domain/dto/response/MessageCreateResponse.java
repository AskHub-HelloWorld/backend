package com.example.ask_hub.team.domain.dto.response;

import com.example.ask_hub.team.domain.entity.Message;
import com.example.ask_hub.team.domain.enums.Role;

public record MessageCreateResponse(
        MessageResponse asker,
        MessageResponse replier
) {
    public static MessageCreateResponse from(Message asker, Message replier) {
        return new MessageCreateResponse(
            new MessageResponse(
                    asker.getId(),
                    asker.getRole()
            ),

            new MessageResponse(
                    replier.getId(),
                    replier.getRole()
            )
        );
    }

    public record MessageResponse(
            Long messageId,
            Role role
    ){
        public static MessageResponse from(Long messageId, Role role) {
            return new MessageResponse(messageId, role);
        }
    }
}
