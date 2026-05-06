package com.example.ask_hub.post.infrastructure;

import com.example.ask_hub.post.domain.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Integer countByPostId(Long postId);
}
