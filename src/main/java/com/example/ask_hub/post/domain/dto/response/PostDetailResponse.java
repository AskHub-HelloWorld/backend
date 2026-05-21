package com.example.ask_hub.post.domain.dto.response;

import com.example.ask_hub.post.domain.entity.Comment;
import com.example.ask_hub.post.domain.entity.Post;
import com.example.ask_hub.user.domain.enums.Position;

import java.time.LocalDateTime;

public record PostDetailResponse(
        String title,
        String content,
        Position position,
        Boolean isResolved,
        String writer,
        LocalDateTime createdAt,
        Integer commentCount,
        ResolvedComment resolvedComment
) {


    public static PostDetailResponse from(Post post, Integer commentCount) {
        return new PostDetailResponse(
                post.getTitle(),
                post.getContent(),
                post.getPosition(),
                post.getIsResolved(),
                post.getIsAnonymous() ? null : post.getUser().getName(),
                post.getCreatedAt(),
                commentCount,
                ResolvedComment.from(post.getComment())
        );
    }

    record ResolvedComment (
            String writer,
            Position position,
            LocalDateTime createdAt,
            String content
    ){
        static ResolvedComment from(Comment comment) {

            if (comment == null) {
                return null;
            }

            return new ResolvedComment(
                    comment.getIsAnonymous() ? null : comment.getUser().getName(),
                    comment.getIsAnonymous() ? null : comment.getUser().getPosition(),
                    comment.getCreatedAt(),
                    comment.getContent()
            );
        }
    }
}
