package com.example.ask_hub.team.domain.dto.response;

import com.example.ask_hub.team.domain.entity.Team;

import java.util.List;

public record TeamGetResponse(
        String name,
        List<Long> conventionIds,
        List<String> userNameList,
        String captainName
) {
    public static TeamGetResponse from(Team team, List<Long> conventionIds, List<String> userNameList, String captainName) {
        return new TeamGetResponse(
                team.getName(),
                conventionIds,
                userNameList,
                captainName
        );
    }
}
