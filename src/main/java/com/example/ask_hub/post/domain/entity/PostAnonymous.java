package com.example.ask_hub.post.domain.entity;

import com.example.ask_hub.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post_anonymouses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostAnonymous {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_anonymous_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Builder
    public PostAnonymous(User user, Post post) {
        this.user = user;
        this.post = post;
    }
}