package com.example.ask_hub.team.domain.dto.response;


public record TeamDownloadResponse(
        String presignedUrl
) {
    public static TeamDownloadResponse from(String presignedUrl) {
        return new TeamDownloadResponse(presignedUrl);
    }
}
