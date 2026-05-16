package com.example.ask_hub.user.domain.dto.response;

public record UserSummaryResponse(
        Integer postCount,
        Integer commentCount,
        Integer point
) {
}
