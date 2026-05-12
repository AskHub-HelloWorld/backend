package com.example.ask_hub.team.domain.dto.response;


public record SessionGetResponse(
        String teamName,
        String messagePreview,
        String captainName
) {
    public static SessionGetResponse from (String teamName, String messagePreview, String captainName) {
        return new SessionGetResponse(teamName, messagePreview, captainName);
    }
}
