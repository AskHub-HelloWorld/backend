package com.example.ask_hub.user.domain.dto.response;

import com.example.ask_hub.user.domain.enums.Position;

import java.util.List;

public record UserSearchResponse(
        List<UserNamePosition> userList
) {
    public record UserNamePosition(
            Long userId,
            String name,
            Position position
    ){
        public static UserNamePosition from(
                Long userId,
                String name,
                Position position
        ){
            return new UserNamePosition(userId, name, position);
        }
    }

    public static UserSearchResponse from(
            List<UserNamePosition> userList
    ){
        return new UserSearchResponse(userList);
    }
}
