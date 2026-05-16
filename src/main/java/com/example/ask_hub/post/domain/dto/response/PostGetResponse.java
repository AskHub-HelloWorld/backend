package com.example.ask_hub.post.domain.dto.response;

import com.example.ask_hub.post.domain.entity.Post;
import com.example.ask_hub.user.domain.entity.User;
import com.example.ask_hub.user.domain.enums.Position;

import java.time.LocalDateTime;

public record PostGetResponse(
        Long postId,

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

    public static PostGetResponse from(Post post, User writer, Long userId, Integer count) {
        return new PostGetResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getPosition(),
                post.getPoint(),
                post.getIsResolved(),
                post.getIsAnonymous() ? null : writer.getName(),
                writer.getId().equals(userId),
                post.getCreatedAt(),
                count
        );
    }

}
