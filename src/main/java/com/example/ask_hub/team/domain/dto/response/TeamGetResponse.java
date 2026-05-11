package com.example.ask_hub.team.domain.dto.response;

import com.example.ask_hub.team.domain.entity.Team;

import java.util.List;

public record TeamGetResponse(
        String name,
        List<ConventionGetResponse> conventions,
        List<String> userNameList,
        String captainName
) {
    public static TeamGetResponse from(Team team, List<ConventionGetResponse> conventions, List<String> userNameList, String captainName) {
        return new TeamGetResponse(
                team.getName(),
                conventions,
                userNameList,
                captainName
        );
    }

    public record ConventionGetResponse(
            Long conventionId,
            String url
    ){
        public static ConventionGetResponse from(Long conventionId, String url) {
            return new ConventionGetResponse(conventionId, url);
        }
    }
}
