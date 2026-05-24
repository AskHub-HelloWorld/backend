package com.example.ask_hub.team.domain.dto.response;

import com.example.ask_hub.team.domain.entity.Convention;
import com.example.ask_hub.team.domain.entity.Team;

import java.util.ArrayList;
import java.util.List;

public record TeamGetResponse(
        String name,
        List<ConventionResponse> conventionResponses,
        List<String> userNameList,
        String captainName
) {
    public static TeamGetResponse from(Team team, List<Convention> conventionList, List<String> userNameList, String captainName) {
        List<ConventionResponse> conventionResponses = new ArrayList<>();
        for (Convention convention : conventionList) {
            conventionResponses.add(new ConventionResponse(convention.getId(), convention.getName()));
        }

        return new TeamGetResponse(
                team.getName(),
                conventionResponses,
                userNameList,
                captainName
        );
    }

    record ConventionResponse(
            Long conventionId,
            String conventionName
    ){
    }
}
