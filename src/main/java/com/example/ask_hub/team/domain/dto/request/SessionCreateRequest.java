package com.example.ask_hub.team.domain.dto.request;

import java.util.List;

public record SessionCreateRequest(
        Long teamId,
        List<Long> userIds
) {
}
