package com.example.ask_hub.post.domain.dto.response;

import com.example.ask_hub.post.domain.entity.Post;
import com.example.ask_hub.user.domain.enums.Position;

import java.time.LocalDateTime;

public record PostGetResponse(
        String title,
        String content,
        Position position,
        Integer point,
        Boolean isResolved,

        String writer, // if anonymous null
        Boolean isMine,
        LocalDateTime createdAt,
        Integer commentCount

) {

    public static PostGetResponse from(Post post, Long userId, Integer count) {
        return new PostGetResponse(
                post.getTitle(),
                post.getContent(),
                post.getPosition(),
                post.getPoint(),
                post.getIsResolved(),
                post.getIsAnonymous() ? null : post.getUser().getName(),
                post.getUser().getId().equals(userId),
                post.getCreatedAt(),
                count
        );
    }

}
