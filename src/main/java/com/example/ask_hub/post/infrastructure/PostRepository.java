package com.example.ask_hub.post.infrastructure;

import com.example.ask_hub.post.domain.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findTop3ByUserIdOrderByCreatedAtDesc(Long userId);

    Integer countByUserId(Long userId);

}
