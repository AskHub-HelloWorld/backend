package com.example.ask_hub.team.domain.dto.request;

public record ConventionGithubRequest(
        String name,
        String githubUrl,
        String branch,
        boolean exist
) {
}
