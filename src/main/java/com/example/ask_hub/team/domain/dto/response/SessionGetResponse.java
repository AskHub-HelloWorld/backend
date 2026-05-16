package com.example.ask_hub.team.domain.dto.response;


public record SessionGetResponse(
        Long teamId,
        Long sessionId,
        String teamName,
        String messagePreview,
        String captainName
) {
    public static SessionGetResponse from (Long teamId, Long sessionId, String teamName, String messagePreview, String captainName) {
        return new SessionGetResponse(teamId, sessionId, teamName, messagePreview, captainName);
    }
}
