package com.example.ask_hub.post.domain.entity;

import com.example.ask_hub.global.domain.BaseEntity;
import com.example.ask_hub.user.domain.entity.User;
import com.example.ask_hub.user.domain.enums.Position;
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Column(nullable = false, length = 30)
    private String title;

    @Column(nullable = false, length = 200)
    private String content;

    @Column(name = "is_anonymous")
    private Boolean isAnonymous;

    @Column(name = "is_resolved")
    private Boolean isResolved;

    @Column
    private Integer point;

    @Column
    private Position position;

    @Builder
    public Post(User user, String title, String content, Boolean isAnonymous, Integer point, Position position) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.isAnonymous = isAnonymous;
        this.isResolved = false;
        this.position = position;
        this.point = point;
    }

    public void select(Comment comment) {
        this.comment = comment;
        this.isResolved = true;
    }
}