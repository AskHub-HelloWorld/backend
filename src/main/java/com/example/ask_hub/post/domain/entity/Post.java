package com.example.ask_hub.post.domain.entity;

import com.example.ask_hub.global.domain.BaseEntity;
import com.example.ask_hub.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 30)
    private String title;

    @Column(nullable = false, length = 200)
    private String context;

    @Column(name = "image_url", length = 30)
    private String imageUrl;

    @Column(name = "is_resolved")
    private Boolean isResolved;

    @Column
    private Integer point;

    @Builder
    public Post(User user, String title, String context, String imageUrl, Integer point) {
        this.user = user;
        this.title = title;
        this.context = context;
        this.imageUrl = imageUrl;
        this.isResolved = false;
        this.point = point;
    }
}