package com.example.ask_hub.post.infrastructure;

import com.example.ask_hub.post.domain.entity.Post;
import com.example.ask_hub.user.domain.enums.Position;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findTop3ByUserIdOrderByCreatedAtDesc(Long userId);

    Integer countByUserId(Long userId);

    Optional<Post> findByTitle(String title);

    Slice<Post> findByTitleContaining(String keyword, Pageable pageable);

    Slice<Post> findAllByPosition(Position category, Pageable pageable);
}