package com.example.ask_hub.post.domain.dto.response;

import com.example.ask_hub.post.domain.entity.Comment;
import com.example.ask_hub.user.domain.enums.Position;

import java.time.LocalDateTime;

public record CommentGetResponse(
        String writer,
        Position position,
        LocalDateTime createdAt,
        String content,
        Boolean isMine
) {
    public static CommentGetResponse from(Comment comment, Long userId) {
        return new CommentGetResponse(
                comment.getIsAnonymous() ? null : comment.getUser().getName(),
                comment.getIsAnonymous() ? null : comment.getUser().getPosition(), // 익명일 때 개발직군 열어둘지?
                comment.getCreatedAt(),
                comment.getContent(),
                comment.getUser().getId().equals(userId)
        );
    }
}
